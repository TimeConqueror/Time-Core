package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockAnimations {
//    public static Animation floroWalk;

    @SubscribeEvent
    public static void registerAnimations(FMLCommonSetupEvent event) {
//        floroWalk = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/floro.walk.json"));
//        floroShoot = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/floro.shoot.json"));
//        floroReveal = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/floro.showing.json"));
//        floroHidden = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/floro.hidden.json"));
//        floroHide = AnimationAPI.register(AnimationAPI.reverse(floroReveal));
    }
}
