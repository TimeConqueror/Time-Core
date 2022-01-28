package ru.timeconqueror.timecore.tests.animation.entity.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AnimTestEntityRegistry.TOWER_GUARDIAN, RenderTowerGuardian::new);
    }
}
