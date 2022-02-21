package ru.timeconqueror.timecore.tests.animation.entity.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TimeModelRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;
import ru.timeconqueror.timecore.tests.animation.entity.client.render.RenderTowerGuardian;
import ru.timeconqueror.timecore.tests.animation.entity.registry.AnimTestEntityRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AnimationTestEntityRenderers {
    @AutoRegistrable
    private static final TimeModelRegister REGISTER = new TimeModelRegister(TimeCore.MODID);
    public static TimeModelLocation TOWER_GUARDIAN = REGISTER.register("models/entity/tower_guardian.json");

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenders(FMLCommonSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(AnimTestEntityRegistry.TOWER_GUARDIAN, RenderTowerGuardian::new);
    }
}
