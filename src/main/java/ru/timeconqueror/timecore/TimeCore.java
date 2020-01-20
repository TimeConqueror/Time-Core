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
import ru.timeconqueror.timecore.api.ITimeMod;
import ru.timeconqueror.timecore.client.resource.TimePackFinder;
import ru.timeconqueror.timecore.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.common.registry.TimeForgeRegistry;

import java.util.Collection;

@Mod(TimeCore.MODID)
public class TimeCore implements ITimeMod {
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

                        if (obj instanceof TimeForgeRegistry<?>) {
                            FMLJavaModLoadingContext.get().getModEventBus().register(obj);
                        } else
                            LOGGER.error("Annotated class with AutoRegistry annotation " + obj.getClass() + " doesn't extend " + TimeForgeRegistry.class.getSimpleName());
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
//        for (ModInfo mod : ModList.get().getMods()) {
//            System.out.println(mod.getModId());
//        }
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
//        System.out.println("doClientStuff");
//        TimeClient.RESOURCE_HOLDER.addDomain(getModID());
//        for (ClientResourcePackInfo allPack : Minecraft.getInstance().getResourcePackList().getAllPacks()) {
//            System.out.println(TextFormatting.AQUA + "Name:" + allPack.getName());
//            System.out.println("Priority:" + allPack.getPriority());
//            System.out.println("Compatibility:" + allPack.getCompatibility());
//            System.out.println("Order Locked:" + allPack.isOrderLocked());
//            System.out.println("Hidden:" + allPack.isHidden());
//            System.out.println("Always enabled:" + allPack.isAlwaysEnabled());
//        }
    }

    @Override
    public String getModID() {
        return MODID;
    }
}
