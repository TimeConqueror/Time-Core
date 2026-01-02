package ru.timeconqueror.timecore.common.registry;

import lombok.extern.log4j.Log4j2;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;

@EventBusSubscriber
@Log4j2
public class TCRegistries {
    public static final ResourceKey<Registry<LevelObjectCodec.Factory<?>>> ANIMATION_NETWORK_DISPATCHER_REGISTRY = ResourceKey.createRegistryKey(TimeCore.rl("animation_network_dispatchers"));

    private static Registry<LevelObjectCodec.Factory<?>> LEVEL_OBJECT_CODEC_REGISTRY;

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent e) {
        LEVEL_OBJECT_CODEC_REGISTRY = e.create(new RegistryBuilder<>(ANIMATION_NETWORK_DISPATCHER_REGISTRY)
                        .sync(true));
    }

    public static Registry<LevelObjectCodec.Factory<?>> levelObjectCodecRegistry() {
        return LEVEL_OBJECT_CODEC_REGISTRY;
    }
}