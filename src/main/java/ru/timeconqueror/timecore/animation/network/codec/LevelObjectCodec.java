package ru.timeconqueror.timecore.animation.network.codec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import ru.timeconqueror.timecore.common.registry.TCRegistries;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class LevelObjectCodec<T> {
    public static StreamCodec<RegistryFriendlyByteBuf, LevelObjectCodec<?>> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public LevelObjectCodec<?> decode(RegistryFriendlyByteBuf buffer) {
            Factory<?> factory = ByteBufCodecs.registry(TCRegistries.ANIMATION_NETWORK_DISPATCHER_REGISTRY).decode(buffer);
            return Objects.requireNonNull((LevelObjectCodec<?>) factory.create(buffer));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, LevelObjectCodec<?> value) {
            ByteBufCodecs.registry(TCRegistries.ANIMATION_NETWORK_DISPATCHER_REGISTRY).encode(buffer, value.getFactory());
            value.encode(buffer);
        }
    };

    @Getter
    private final Factory<? extends T> factory;

    protected abstract void encode(FriendlyByteBuf buffer);

    public abstract T construct(Level level);

    public interface Factory<T> {
        LevelObjectCodec<T> create(T object);

        LevelObjectCodec<T> create(FriendlyByteBuf buffer);
    }
}
