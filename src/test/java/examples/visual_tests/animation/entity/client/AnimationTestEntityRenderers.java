package examples.visual_tests.animation.entity.client;

import examples.visual_tests.animation.entity.client.render.RenderDebugBeacons;
import examples.visual_tests.animation.entity.client.render.RenderTowerGuardian;
import examples.visual_tests.animation.entity.registry.AnimTestEntityRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TimeModelRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.client.render.model.InFileLocation;

@EventBusSubscriber
public class AnimationTestEntityRenderers {
    @AutoRegistrable
    private static final TimeModelRegister REGISTER = new TimeModelRegister(TimeCore.MODID);
    public static InFileLocation TOWER_GUARDIAN = REGISTER.register("models/entity/tower_guardian.json");

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AnimTestEntityRegistry.TOWER_GUARDIAN, RenderTowerGuardian::new);
        event.registerEntityRenderer(AnimTestEntityRegistry.DEBUG_BEACONS, RenderDebugBeacons::new);
    }
}
