package ru.timeconqueror.timecore;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.datagen.DataGen;
import ru.timeconqueror.timecore.client.resource.TimePackFinder;
import ru.timeconqueror.timecore.devtools.StructureRevealer;
import ru.timeconqueror.timecore.util.I18nResolver;
import ru.timeconqueror.timecore.util.reflection.ReflectionHelper;

@Mod(TimeCore.MODID)
public final class TimeCore extends TimeMod {
    public static final String MODID = "timecore";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final I18nResolver LANG_RESOLVER = new I18nResolver(MODID);
    public static TimeCore INSTANCE = null;

    public TimeCore() {
        INSTANCE = this;

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();

            //noinspection ConstantConditions
            if (mc != null) {//it's null in runData
                mc.getResourcePackList().addPackFinder(new TimePackFinder());
            }
        });

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDataEvent);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ReflectionHelper.initClass(StructureRevealer.class);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    private void onDataEvent(GatherDataEvent event) {
        DataGen.disableFileDeletion = true;
    }
}
