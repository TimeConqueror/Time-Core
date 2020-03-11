package ru.timeconqueror.timecore.misc;

import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.Initable;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

public class ModInitializer {
    public static void setupAutoRegistries() {
        for (ModInfo modInfo : ModList.get().getMods()) {
            if (modInfo.getModId().equals(ModLoadingContext.get().getActiveNamespace())) {
                ModFileScanData scanData = modInfo.getOwningFile().getFile().getScanResult();

                scanData.getAnnotations().stream()
                        .filter(annotationData -> annotationData.getAnnotationType().equals(TimeAutoRegistrable.ASM_TYPE))
                        .forEach(annotationData -> {
                            try {
                                final Class<?> regClass = Class.forName(annotationData.getClassType().getClassName());

                                final ModAnnotation.EnumHolder targetHolder = (ModAnnotation.EnumHolder) annotationData.getAnnotationData().getOrDefault("target", new ModAnnotation.EnumHolder(null, "INSTANCE"));
                                TimeAutoRegistrable.Target target = TimeAutoRegistrable.Target.valueOf(targetHolder.getValue());

                                Lazy<?> instance = Lazy.of(() -> {
                                    try {
                                        return regClass.newInstance();
                                    } catch (ReflectiveOperationException e) {
                                        if (e.getCause() instanceof NoSuchMethodException) {
                                            throw new RuntimeException("TimeCore AutoRegistry can't find constructor with no parameters for " + regClass, e);
                                        } else {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });

                                if (target == TimeAutoRegistrable.Target.CLASS) {
                                    FMLJavaModLoadingContext.get().getModEventBus().register(regClass);
                                    TimeCore.LOGGER.debug("{}: Registered Event Subscriber as class: {}", ModLoadingContext.get().getActiveNamespace(), regClass);
                                } else {
                                    FMLJavaModLoadingContext.get().getModEventBus().register(instance.get());
                                    TimeCore.LOGGER.debug("{}: Registered Event Subscriber as instance: {}", ModLoadingContext.get().getActiveNamespace(), regClass);
                                }

                                if (Initable.class.isAssignableFrom(regClass)) {//TODO remove
                                    FMLJavaModLoadingContext.get().getModEventBus().addListener(((Initable) instance.get())::onInit);
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        });

                break;
            }
        }
    }
}
