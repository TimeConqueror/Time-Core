package ru.timeconqueror.timecore.animation.network.codec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;
import ru.timeconqueror.timecore.common.registry.TCRegistries;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class LevelObjectCodec<T> {
    @Getter
    private final Factory<? extends T> factory;

    @SuppressWarnings("unchecked")
    public static <T> LevelObjectCodec<T> read(FriendlyByteBuf buffer) {
        IForgeRegistry<LevelObjectCodec.Factory<?>> registry = TCRegistries.levelObjectCodecRegistry();
        Factory<?> factory = buffer.readRegistryIdUnsafe(registry);
        return Objects.requireNonNull((LevelObjectCodec<T>) factory.create(buffer));
    }

    public static void write(LevelObjectCodec<?> codec, FriendlyByteBuf buffer) {
        IForgeRegistry<LevelObjectCodec.Factory<?>> registry = TCRegistries.levelObjectCodecRegistry();
        buffer.writeRegistryIdUnsafe(registry, codec.getFactory());
        codec.encode(buffer);
    }

    protected abstract void encode(FriendlyByteBuf buffer);

    public abstract T construct(Level level);

    public interface Factory<T> {
        LevelObjectCodec<T> create(T object);

        LevelObjectCodec<T> create(FriendlyByteBuf buffer);
    }
}
