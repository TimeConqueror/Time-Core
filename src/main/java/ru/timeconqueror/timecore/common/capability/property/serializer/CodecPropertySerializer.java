package ru.timeconqueror.timecore.common.capability.property.serializer;

import com.mojang.serialization.Codec;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.CodecUtils;

@RequiredArgsConstructor
public class CodecPropertySerializer<T> implements IPropertySerializer<T> {
    private final Codec<T> codec;

    @Override
    public void serialize(@NotNull String name, T value, @NotNull CompoundTag nbt) {
        Tag tag = CodecUtils.encodeStrictly(codec, CodecUtils.NBT_OPS, value);
        nbt.put(name, tag);
    }

    @Override
    public T deserialize(@NotNull String name, @NotNull CompoundTag nbt) {
        return CodecUtils.decodeStrictly(codec, CodecUtils.NBT_OPS, nbt.get(name));
    }
}
