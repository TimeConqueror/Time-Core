//package ru.timeconqueror.timecore.animation_example.block_example.registry;
//
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
//import ru.timeconqueror.timecore.TimeCore;
//import ru.timeconqueror.timecore.api.animation.Animation;
//import ru.timeconqueror.timecore.api.animation.AnimationAPI;
//
//@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//public class TileAnimations {
//    public static Animation heatCubeIdle;
//
//    @SubscribeEvent
//    public static void registerAnimations(FMLCommonSetupEvent event) {
//        heatCubeIdle = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/spark_smelter.idle.json"));
//    }
//}
