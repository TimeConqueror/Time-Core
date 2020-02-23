package ru.timeconqueror.timecore.api;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.event.FMLModConstructedEvent;
import ru.timeconqueror.timecore.api.common.registry.ForgeTimeRegistry;
import ru.timeconqueror.timecore.api.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.api.util.Wrapper;

/**
 * Base mod class needed for TimeCore features.
 * <p>
 * Your main mod class should extend this if you want to apply such features as auto-registry.
 */
public abstract class TimeMod {
    public TimeMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConstructed);
    }

    private void setupAutoRegistries() {
        for (ModInfo modInfo : ModList.get().getMods()) {
            if (modInfo.getModId().equals(getModID())) {
                ModFileScanData scanData = modInfo.getOwningFile().getFile().getScanResult();

                Wrapper<Boolean> loaded = new Wrapper<>(false);
                scanData.getAnnotations().stream()
                        .filter(annotationData -> annotationData.getAnnotationType().equals(TimeAutoRegistry.ASM_TYPE))
                        .forEach(annotationData -> {
                            Class<?> regClass = null;
                            try {
                                regClass = Class.forName(annotationData.getClassType().getClassName());

                                Object obj = regClass.newInstance();

                                if (obj instanceof ForgeTimeRegistry<?>) {
                                    FMLJavaModLoadingContext.get().getModEventBus().register(obj);
                                    loaded.set(true);
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

                if (loaded.get()) {
                    TimeCore.LOGGER.info("Loaded Auto-Registries for {} ({})", modInfo.getDisplayName(), modInfo.getModId());
                }

                break;
            }
        }
    }

    private void onModConstructed(FMLModConstructedEvent event) {
        setupAutoRegistries();
    }

    public abstract String getModID();
}
