package ru.timeconqueror.timecore.common.registry;

import lombok.extern.log4j.Log4j2;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Log4j2
public class TCRegistries {
    public static final ResourceKey<Registry<LevelObjectCodec.Factory<?>>> ANIMATION_NETWORK_DISPATCHER_REGISTRY = ResourceKey.createRegistryKey(TimeCore.rl("animation_network_dispatchers"));

    private static Supplier<IForgeRegistry<LevelObjectCodec.Factory<?>>> LEVEL_OBJECT_CODEC_REGISTRY;

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent e) {
        LEVEL_OBJECT_CODEC_REGISTRY = e.create(new RegistryBuilder<LevelObjectCodec.Factory<?>>()
                .setName(ANIMATION_NETWORK_DISPATCHER_REGISTRY.location())
                .disableSaving());
    }

    public static IForgeRegistry<LevelObjectCodec.Factory<?>> levelObjectCodecRegistry() {
        return LEVEL_OBJECT_CODEC_REGISTRY.get();
    }
}