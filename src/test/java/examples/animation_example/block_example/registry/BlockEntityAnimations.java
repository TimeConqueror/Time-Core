package examples.animation_example.block_example.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationAPI;

@EventBusSubscriber
public class BlockEntityAnimations {
    public static Animation heatCubeIdle;

    @SubscribeEvent
    public static void registerAnimations(FMLCommonSetupEvent event) {
        heatCubeIdle = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/spark_smelter.idle.json"));
    }
}
