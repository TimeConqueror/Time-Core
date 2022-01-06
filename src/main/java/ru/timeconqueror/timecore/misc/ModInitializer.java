package ru.timeconqueror.timecore.misc;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.GlobalResourceStorage;
import ru.timeconqueror.timecore.api.devtools.kotlin.KotlinAutomaticEventSubscriber;
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.reflection.UnlockedField;
import ru.timeconqueror.timecore.api.reflection.UnlockedMethod;
import ru.timeconqueror.timecore.api.reflection.provider.ClassHandler;
import ru.timeconqueror.timecore.api.reflection.provider.ClassHandlers;
import ru.timeconqueror.timecore.api.registry.ForgeRegister;
import ru.timeconqueror.timecore.api.registry.TimeRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable.InitMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraftforge.fml.Logging.LOADING;

public class ModInitializer {
    private static final Type TIME_AUTO_REG_TYPE = Type.getType(AutoRegistrable.class);
    private static final Type TIME_AUTO_REG_INIT_TYPE = Type.getType(InitMethod.class);

    public static synchronized void run(ModContainer modContainer, ModFileScanData scanResults, Object mod) {
        TimeCore.LOGGER.debug("Setting up TimeCore components for {}", modContainer.getModId());

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(EventPriority.HIGHEST, (FMLConstructModEvent event) -> {
            if (!modContainer.matches(mod)) {
                throw new IllegalArgumentException(String.format("Object being provided as mod (%s) doesn't match the one (%s) in the container.", mod.getClass(), modContainer.getMod().getClass()));
            }
        });

        String modId = modContainer.getModId();

        runKotlinAutomaticEventSubscriber(modId, modContainer, scanResults, mod.getClass());

        List<Runnable> initMethods = new ArrayList<>();
        List<TimeRegister> registers = new ArrayList<>();

        setupAutoRegistries(scanResults, initMethods::add, registers::add);

        RegisterSubscriber.regToBus(registers, modEventBus);

        processInitMethods(initMethods);

        GlobalResourceStorage.INSTANCE.setup(modId);
    }

    private static void runKotlinAutomaticEventSubscriber(String modId, ModContainer modContainer, ModFileScanData scanResults, Class<?> modClass) {
        TimeCore.LOGGER.debug(LOADING, "Injecting Automatic event subscribers for {}", modId);
        KotlinAutomaticEventSubscriber.inject(modContainer, scanResults, modClass.getClassLoader());
        TimeCore.LOGGER.debug(LOADING, "Completed Automatic event subscribers for {}", modId);
    }

    private static void setupAutoRegistries(ModFileScanData scanResults, Consumer<Runnable> initMethodRegistrator, Consumer<TimeRegister> registerSubscriber) {
        scanResults.getAnnotations().stream()
                .filter(annotationData -> annotationData.annotationType().equals(TIME_AUTO_REG_TYPE) || annotationData.annotationType().equals(TIME_AUTO_REG_INIT_TYPE))
                .forEach(annotationData -> {
                    try {
                        String containerClassName = annotationData.clazz().getClassName();
                        Class<?> containerClass;
                        try {
                            containerClass = Class.forName(containerClassName);
                        } catch (Throwable e) {
                            throw new RuntimeException(String.format("There was an exception while trying to load %s", containerClassName), e);
                        }

                        if (annotationData.annotationType().equals(TIME_AUTO_REG_TYPE)) {
                            processAutoRegistrable(containerClass, annotationData, registerSubscriber);
                        } else {
                            processTimeAutoRegInitMethod(containerClass, annotationData, initMethodRegistrator);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static void processAutoRegistrable(Class<?> containerClass, ModFileScanData.AnnotationData annotationData, Consumer<TimeRegister> registerSubscriber) throws ClassNotFoundException {
        String fieldName = annotationData.memberName();
        UnlockedField<?, Object> field = ReflectionHelper.findField(containerClass, fieldName);

        processAutoRegistrableOnField(containerClass, field, registerSubscriber);
    }

    private static void processAutoRegistrableOnField(Class<?> containerClass, UnlockedField<?, Object> field, Consumer<TimeRegister> registerSubscriber) {
        if (field.isStatic()) {
            if (TimeRegister.class.isAssignableFrom(field.unboxed().getType())) {
                TimeRegister register = (TimeRegister) field.get(null);
                register.setOwner(containerClass);

                registerSubscriber.accept(register);
            } else {
                throw new UnsupportedOperationException(AutoRegistrable.class.getSimpleName() + " can be used only on fields that have " + ForgeRegister.class.getSimpleName() + " type. Error is in: " + field);
            }
        } else {
            throw new UnsupportedOperationException(AutoRegistrable.class.getSimpleName() + " can be used only on static fields. Error is in: " + field);
        }
    }

    private static void processTimeAutoRegInitMethod(Class<?> containerClass, ModFileScanData.AnnotationData annotationData, Consumer<Runnable> preConstructMethodRegistrator) throws ClassNotFoundException {
        String methodSignature = annotationData.memberName();

        ClassHandler handler = ClassHandlers.findHandler(containerClass);
        if (handler == null) {
            throw new IllegalArgumentException("Can't handle class " + containerClass.getName() + ", because there's no " + ClassHandler.class.getName() + " found for it.");
        }

        UnlockedMethod<?, Object> initMethod = handler.findMethod(containerClass, methodSignature);
        if (initMethod == null)
            throw new NoSuchMethodError("Not found method " + methodSignature + " from class " + containerClass.getName());
        handler.requireStatic(initMethod);

        Method nativeMethod = initMethod.unboxed();
        if (nativeMethod.getParameterCount() == 0) {
            preConstructMethodRegistrator.accept(() -> handler.invokeStaticMethod(initMethod));
        } else if (nativeMethod.getParameterCount() == 1 && FMLConstructModEvent.class.isAssignableFrom(nativeMethod.getParameterTypes()[0])) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.HIGHEST, (FMLConstructModEvent event) -> handler.invokeStaticMethod(initMethod, event));
        } else {
            throw new UnsupportedOperationException(InitMethod.class.getSimpleName() + " can be used only on methods with " + FMLConstructModEvent.class.getName() + " parameter or without any parameters. Error is in: " + initMethod);
        }
    }

    private static void processInitMethods(List<Runnable> initMethods) {
        initMethods.forEach(Runnable::run);
    }
}
