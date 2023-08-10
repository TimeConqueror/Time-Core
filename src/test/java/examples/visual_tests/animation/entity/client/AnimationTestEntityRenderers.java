package examples.visual_tests.animation.entity.client;

import examples.visual_tests.animation.entity.client.render.RenderTowerGuardian;
import examples.visual_tests.animation.entity.registry.AnimTestEntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TimeModelRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.client.render.model.InFileLocation;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AnimationTestEntityRenderers {
    @AutoRegistrable
    private static final TimeModelRegister REGISTER = new TimeModelRegister(TimeCore.MODID);
    public static InFileLocation TOWER_GUARDIAN = REGISTER.register("models/entity/tower_guardian.json");

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AnimTestEntityRegistry.TOWER_GUARDIAN, RenderTowerGuardian::new);
    }
}
