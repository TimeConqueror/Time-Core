package examples.visual_tests.animation.entity.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationAPI;

@EventBusSubscriber
public class AnimTestEntityAnimations {
    public static Animation towerGuardianWalk;

    @SubscribeEvent
    public static void registerAnimations(FMLCommonSetupEvent event) {
        towerGuardianWalk = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/tower_guardian.walk.json"));
    }
}
