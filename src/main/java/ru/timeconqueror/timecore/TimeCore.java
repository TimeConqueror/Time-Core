package ru.timeconqueror.timecore;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.common.registry.ForgeTimeRegistry;
import ru.timeconqueror.timecore.api.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.client.resource.TimePackFinder;

import java.util.Collection;

@Mod(TimeCore.MODID)
public class TimeCore extends TimeMod {
    public static final String MODID = "timecore";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static TimeCore INSTANCE = null;

    public TimeCore() {
        INSTANCE = this;

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().getResourcePackList().addPackFinder(new TimePackFinder()));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        setupAutoRegistries();
    }

    private void setupAutoRegistries() {
        ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(annotationData -> annotationData.getAnnotationType().equals(TimeAutoRegistry.ASM_TYPE))
                .forEach(annotationData -> {

                    Class<?> regClass = null;
                    try {
                        regClass = Class.forName(annotationData.getClassType().getClassName());

                        Object obj = regClass.newInstance();

                        if (obj instanceof ForgeTimeRegistry<?>) {
                            FMLJavaModLoadingContext.get().getModEventBus().register(obj);
                        } else
                            LOGGER.error("Annotated class with AutoRegistry annotation " + obj.getClass() + " doesn't extend " + ForgeTimeRegistry.class.getSimpleName());
                    } catch (InstantiationException e) {
                        if (e.getCause() instanceof NoSuchMethodException) {
                            LOGGER.error("TimeCore AutoRegistry can't find constructor with no parameters for " + regClass, e);
                        } else e.printStackTrace();
                    } catch (ClassNotFoundException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    @Override
    public String getModID() {
        return MODID;
    }
}
