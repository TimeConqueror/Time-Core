package ru.timeconqueror.timecore.misc;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.GlobalResourceStorage;
import ru.timeconqueror.timecore.api.devtools.kotlin.KotlinAutomaticEventSubscriber;
import ru.timeconqueror.timecore.api.registry.ForgeRegister;
import ru.timeconqueror.timecore.api.registry.TimeRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable.InitMethod;
import ru.timeconqueror.timecore.api.util.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.util.reflection.UnlockedField;
import ru.timeconqueror.timecore.api.util.reflection.UnlockedMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraftforge.fml.Logging.LOADING;

public class ModInitializer {
    private static final Type TIME_AUTO_REG_TYPE = Type.getType(AutoRegistrable.class);
    private static final Type TIME_AUTO_REG_INIT_TYPE = Type.getType(InitMethod.class);

    public static synchronized void run(String modId, ModContainer modContainer, ModFileScanData scanResults, Class<?> modClass) {
        runKotlinAutomaticEventSubscriber(modId, modContainer, scanResults, modClass);

        List<UnlockedMethod<?>> initMethods = new ArrayList<>();
        List<TimeRegister> registers = new ArrayList<>();

        setupAutoRegistries(scanResults, initMethods::add, registers::add);

        RegisterSubscriber.regToBus(registers, FMLJavaModLoadingContext.get().getModEventBus());

        processInitMethods(initMethods);

        GlobalResourceStorage.INSTANCE.setup(modId);
    }

    private static void runKotlinAutomaticEventSubscriber(String modId, ModContainer modContainer, ModFileScanData scanResults, Class<?> modClass) {
        TimeCore.LOGGER.debug(LOADING, "Injecting Automatic event subscribers for {}", modId);
        KotlinAutomaticEventSubscriber.inject(modContainer, scanResults, modClass.getClassLoader());
        TimeCore.LOGGER.debug(LOADING, "Completed Automatic event subscribers for {}", modId);
    }

    private static void setupAutoRegistries(ModFileScanData scanResults, Consumer<UnlockedMethod<?>> initMethodRegistrator, Consumer<TimeRegister> registerSubscriber) {
        scanResults.getAnnotations().stream()
                .filter(annotationData -> annotationData.getAnnotationType().equals(TIME_AUTO_REG_TYPE) || annotationData.getAnnotationType().equals(TIME_AUTO_REG_INIT_TYPE))
                .forEach(annotationData -> {
                    try {
                        String containerClassName = annotationData.getClassType().getClassName();
                        Class<?> containerClass;
                        try {
                            containerClass = Class.forName(containerClassName);
                        } catch (Throwable e) {
                            throw new RuntimeException(String.format("There was an exception while trying to load %s", containerClassName));
                        }

                        if (annotationData.getAnnotationType().equals(TIME_AUTO_REG_TYPE)) {
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
        String fieldName = annotationData.getMemberName();
        UnlockedField<Object> field = ReflectionHelper.findField(containerClass, fieldName);

        processAutoRegistrableOnField(containerClass, field, registerSubscriber);
    }

    private static void processAutoRegistrableOnField(Class<?> containerClass, UnlockedField<Object> field, Consumer<TimeRegister> registerSubscriber) {
        if (field.isStatic()) {
            if (TimeRegister.class.isAssignableFrom(field.getField().getType())) {
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

    private static void processTimeAutoRegInitMethod(Class<?> containerClass, ModFileScanData.AnnotationData annotationData, Consumer<UnlockedMethod<?>> preConstructMethodRegistrator) throws ClassNotFoundException {
        String methodSignature = annotationData.getMemberName();

        StringBuilder methodName = new StringBuilder();
        for (char c : methodSignature.toCharArray()) {
            if (c == '(') break;

            methodName.append(c);
        }

        UnlockedMethod<?> initMethod = ReflectionHelper.findMethod(containerClass, methodName.toString());

        if (initMethod.isStatic()) {
            Method nativeMethod = initMethod.getMethod();
            if (nativeMethod.getParameterCount() == 0) {
                preConstructMethodRegistrator.accept(initMethod);
            } else if (nativeMethod.getParameterCount() == 1 && FMLConstructModEvent.class.isAssignableFrom(nativeMethod.getParameterTypes()[0])) {
                FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.HIGHEST, event -> initMethod.invoke(null, event));//TODO seems this will be called on every event
            } else {
                throw new UnsupportedOperationException(InitMethod.class.getSimpleName() + " can be used only on methods without any parameters. Error is in: " + initMethod);
            }
        } else {
            throw new UnsupportedOperationException(InitMethod.class.getSimpleName() + " can be used only on static fields. Error is in: " + initMethod);
        }
    }

    private static void processInitMethods(List<UnlockedMethod<?>> initMethods) {
        initMethods.forEach(unlockedMethod -> unlockedMethod.invoke(null));
    }
}
