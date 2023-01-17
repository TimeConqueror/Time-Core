package ru.timeconqueror.timecore;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import ru.timeconqueror.timecore.animation.AnimationRegistry;
import ru.timeconqueror.timecore.animation.watcher.TransitionWatcher;
import ru.timeconqueror.timecore.api.Markers;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityManager;
import ru.timeconqueror.timecore.devtools.StructureRevealer;

@Mod(TimeCore.MODID)//todo add null check in ObjectHolder
//TODO add readable exception when there's no animation file, for now it's just nullpointer
public final class TimeCore implements TimeMod {
    public static final String MODID = "timecore";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static TimeCore INSTANCE = null;

    private static final String MARKER_PROPERTY = "timecore.logging.markers";

    private final CoffeeCapabilityManager capabilityManager;

    public TimeCore() {
        INSTANCE = this;

        checkForMixinBootstrap();

        capabilityManager = new CoffeeCapabilityManager();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConstruct);
    }

    /**
     * Creates ResourceLocation with bound mod id.
     */
    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    private void onConstruct(FMLConstructModEvent event) {
        EnvironmentUtils.handleMarkerVisibility(TimeCore.MODID, MARKER_PROPERTY, Markers.getAll());
    }

    private void setup(final FMLCommonSetupEvent event) {
        ReflectionHelper.initClass(StructureRevealer.class);
        event.enqueueWork(capabilityManager::addDefaultAttachers);

        AnimationRegistry.registerAnimation(Animation.NULL);
        AnimationRegistry.registerAnimation(TransitionWatcher.TRANSITION);
    }

    private static void checkForMixinBootstrap() {
        try {
            if (MixinEnvironment.getCurrentEnvironment().getPhase() != MixinEnvironment.Phase.DEFAULT) {
                throw new IllegalArgumentException("Mixins are not initialized");
            }
        } catch (NoClassDefFoundError e) {
            throw new IllegalStateException("TimeCore requires MixinBootstrap Mod to be loaded.");
        }
    }

    public CoffeeCapabilityManager getCapabilityManager() {
        return capabilityManager;
    }
}
