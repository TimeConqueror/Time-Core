package ru.timeconqueror.timecore.api;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import ru.timeconqueror.timecore.api.common.event.FMLModConstructedEvent;
import ru.timeconqueror.timecore.api.common.registry.ForgeTimeRegistry;
import ru.timeconqueror.timecore.api.common.registry.TimeAutoRegistry;

public abstract class TimeMod {
    public TimeMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConstructed);
    }

    private static void setupAutoRegistries() {
        ModList.get().getMods().forEach(modInfo -> {
            ModFileScanData scanData = modInfo.getOwningFile().getFile().getScanResult();

            scanData.getAnnotations().stream()
                    .filter(annotationData -> annotationData.getAnnotationType().equals(TimeAutoRegistry.ASM_TYPE))
                    .forEach(annotationData -> {
                        Class<?> regClass = null;
                        try {
                            regClass = Class.forName(annotationData.getClassType().getClassName());

                            Object obj = regClass.newInstance();

                            if (obj instanceof ForgeTimeRegistry<?>) {
                                FMLJavaModLoadingContext.get().getModEventBus().register(obj);
                            } else
                                throw new RuntimeException("Annotated class with AutoRegistry annotation " + obj.getClass() + " doesn't extend " + ForgeTimeRegistry.class.getSimpleName());

                        } catch (ReflectiveOperationException e) {
                            if (e.getCause() instanceof NoSuchMethodException) {
                                throw new RuntimeException("TimeCore AutoRegistry can't find constructor with no parameters for " + regClass, e);
                            } else {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        });
    }

    public void onModConstructed(FMLModConstructedEvent event) {
        setupAutoRegistries();
    }

    public abstract String getModID();
}
