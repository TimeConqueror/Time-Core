package ru.timeconqueror.timecore.mod.misc;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.devtools.kotlin.KotlinAutomaticEventSubscriber;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.deferred.base.DeferredFMLImplForgeRegister;
import ru.timeconqueror.timecore.registry.deferred.base.DeferredTimeRegister;
import ru.timeconqueror.timecore.util.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.util.reflection.UnlockedField;

import java.lang.annotation.ElementType;

import static net.minecraftforge.fml.Logging.LOADING;

public class ModInitializer {
    public static void run(String modId, ModContainer modContainer, ModFileScanData scanResults, Class<?> modClass) {
        runKotlinAutomaticEventSubscriber(modId, modContainer, scanResults, modClass);
        setupAutoRegistries(scanResults);
        regModBusListeners();
    }

    private static void runKotlinAutomaticEventSubscriber(String modId, ModContainer modContainer, ModFileScanData scanResults, Class<?> modClass) {
        TimeCore.LOGGER.debug(LOADING, "Injecting Automatic event subscribers for {}", modId);
        KotlinAutomaticEventSubscriber.inject(modContainer, scanResults, modClass.getClassLoader());
        TimeCore.LOGGER.debug(LOADING, "Completed Automatic event subscribers for {}", modId);
    }

    private static void regModBusListeners() {
        FMLJavaModLoadingContext.get().getModEventBus().register(LangGeneratorFacade.class);
    }

    private static void setupAutoRegistries(ModFileScanData scanResults) {
        scanResults.getAnnotations().stream()
                .filter(annotationData -> annotationData.getAnnotationType().equals(TimeAutoRegistrable.ASM_TYPE))
                .forEach(annotationData -> {
                    try {
                        String containerClassName = annotationData.getClassType().getClassName();
                        Class<?> containerClass = Class.forName(containerClassName);

                        ElementType targetType = annotationData.getTargetType();

                        if (targetType == ElementType.FIELD) {
                            String memberName = annotationData.getMemberName();
                            UnlockedField<Object> field = ReflectionHelper.findFieldUnsuppressed(containerClass, memberName);

                            processAnnoOnField(containerClass, field);
                        } else if (targetType == ElementType.TYPE) {

                            final ModAnnotation.EnumHolder targetHolder = (ModAnnotation.EnumHolder) annotationData.getAnnotationData().getOrDefault("target", new ModAnnotation.EnumHolder(null, "INSTANCE"));
                            TimeAutoRegistrable.Target target = TimeAutoRegistrable.Target.valueOf(targetHolder.getValue());

                            Object instance;
                            try {
                                instance = containerClass.newInstance();
                            } catch (ReflectiveOperationException e) {
                                if (e.getCause() instanceof NoSuchMethodException) {
                                    throw new RuntimeException("TimeCore AutoRegistry can't find constructor with no parameters for " + containerClass, e);
                                } else {
                                    throw new RuntimeException(e);
                                }
                            }

                            if (target == TimeAutoRegistrable.Target.CLASS) {
                                FMLJavaModLoadingContext.get().getModEventBus().register(containerClass);
                                TimeCore.LOGGER.debug("{}: Registered Event Subscriber as class: {}", ModLoadingContext.get().getActiveNamespace(), containerClass);
                            } else {
                                FMLJavaModLoadingContext.get().getModEventBus().register(instance);
                                TimeCore.LOGGER.debug("{}: Registered Event Subscriber as instance: {}", ModLoadingContext.get().getActiveNamespace(), containerClass);
                            }

                            String instanceFieldName = (String) annotationData.getAnnotationData().getOrDefault("instance", "");
                            if (!instanceFieldName.isEmpty()) {
                                UnlockedField<Object> field = ReflectionHelper.findFieldUnsuppressed(containerClass, instanceFieldName);
                                if (field.isStatic()) {
                                    field.set(containerClass, instance);
                                } else {
                                    throw new IllegalStateException("Field with location " + instanceFieldName + " in class " + containerClass + " should be static!");
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static void processAnnoOnField(Class<?> containerClass, UnlockedField<Object> field) {
        if (field.isStatic() && DeferredTimeRegister.class.isAssignableFrom(field.getField().getType())) {
            DeferredTimeRegister register = ((DeferredTimeRegister) field.get(null));
            register.regToBus(FMLJavaModLoadingContext.get().getModEventBus());
        } else {
            throw new UnsupportedOperationException(TimeAutoRegistrable.class.getSimpleName() + " can be used only on fields that are static and have " + DeferredFMLImplForgeRegister.class.getSimpleName() + " type");
        }
    }
}
