package ru.timeconqueror.timecore.api.util;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 */
public interface INBTSimpleSerializable<T extends Tag>
{
    T serializeNBT();
    void deserializeNBT(T nbt);

    /**
     * Adapter, which allows {@link INBTSimpleSerializable} to be used as NeoForge's {@link INBTSerializable}
     */
    static <T extends Tag> INBTSerializableAdapter<T> adapter(INBTSimpleSerializable<T> delegate) {
        return new DelegatedAdapter<>(delegate);
    }

    interface INBTSerializableAdapter<T extends Tag> extends INBTSerializable<T>, INBTSimpleSerializable<T> {
        @Override
        @UnknownNullability
        default T serializeNBT(HolderLookup.Provider provider) {
            return serializeNBT();
        }

        @Override
        default void deserializeNBT(HolderLookup.Provider provider, T nbt) {
            deserializeNBT(nbt);
        }
    }

    @RequiredArgsConstructor
    class DelegatedAdapter<T extends Tag> implements INBTSerializableAdapter<T>{
        private final INBTSimpleSerializable<T> delegate;

        @Override
        public T serializeNBT() {
            return delegate.serializeNBT();
        }

        @Override
        public void deserializeNBT(T nbt) {
            delegate.deserializeNBT(nbt);
        }
    }
}