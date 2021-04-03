package ru.timeconqueror.timecore.tests.animation.entity.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationAPI;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAnimations {
    public static Animation towerGuardianWalk;

    @SubscribeEvent
    public static void registerAnimations(FMLCommonSetupEvent event) {
        towerGuardianWalk = AnimationAPI.loadAndRegisterAnimation(TimeCore.rl("animations/tower_guardian.walk.json"));
    }
}
