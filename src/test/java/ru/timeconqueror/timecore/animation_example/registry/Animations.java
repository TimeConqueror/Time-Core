package ru.timeconqueror.timecore.animation_example.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationAPI;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Animations {
    public static Animation floroWalk;
    public static Animation floroShoot;
    public static Animation floroReveal;
    public static Animation floroHidden;
    public static Animation floroHide;

    @SubscribeEvent
    public static void registerAnimations(FMLCommonSetupEvent event) {
        floroWalk = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/floro.walk.json"));
        floroShoot = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/floro.shoot.json"));
        floroReveal = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/floro.showing.json"));
        floroHidden = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/floro.hidden.json"));
        floroHide = AnimationAPI.register(AnimationAPI.reverse(floroReveal));
    }
}
