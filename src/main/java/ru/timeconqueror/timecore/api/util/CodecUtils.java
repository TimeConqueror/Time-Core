package ru.timeconqueror.timecore.api.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.lang.reflect.Array;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CodecUtils {
    public static final NbtOps NBT_OPS = NbtOps.INSTANCE;
    public static final JsonOps JSON_OPS = JsonOps.INSTANCE;

    public static <T, SERIALIZED> T decodeSoftly(Codec<T> codec, DynamicOps<SERIALIZED> ops, SERIALIZED input, T defaultVal) {
        return codec.decode(ops, input).result().map(Pair::getFirst).orElse(defaultVal);
    }

    public static <T, SERIALIZED> T decodeStrictly(Codec<T> codec, DynamicOps<SERIALIZED> ops, SERIALIZED input) {
        return codec.decode(ops, input).result().map(Pair::getFirst).orElseThrow(NotFoundException::new);
    }

    public static <T, SERIALIZED> void encodeSoftly(Codec<T> codec, DynamicOps<SERIALIZED> ops, T input, Consumer<SERIALIZED> actionIfProvided) {
        codec.encodeStart(ops, input).result().ifPresent(actionIfProvided);
    }

    public static <T, SERIALIZED> SERIALIZED encodeStrictly(Codec<T> codec, DynamicOps<SERIALIZED> ops, T input) {
        return codec.encodeStart(ops, input).result().orElseThrow(NotFoundException::new);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[][] read2DimArr(CompoundTag tableTag, Class<T> elementClass, Codec<T> elementCodec) {
        int size = tableTag.getInt("size");

        T[][] table = null;

        for (int i = 0; i < size; i++) {
            CompoundTag columnTag = tableTag.getCompound(Integer.toString(i));
            int columnSize = columnTag.getInt("size");

            T[] column = (T[]) Array.newInstance(elementClass, columnSize);

            for (int j = 0; j < columnSize; j++) {
                if (columnTag.contains(Integer.toString(j))) {
                    Tag elementTag = columnTag.get(Integer.toString(j));

                    T element = decodeStrictly(elementCodec, NBT_OPS, elementTag);
                    column[j] = element;
                }
            }

            if (i == 0) {
                Class<?> columnClass = column.getClass();
                table = (T[][]) Array.newInstance(columnClass, size);
            }

            table[i] = column;
        }

        return table;
    }

    public static <T> CompoundTag write2DimArr(T[][] objArr, Codec<T> elementCodec) {
        return write2DimArr(objArr, elementCodec, e -> true);
    }

    public static <T> CompoundTag write2DimArr(T[][] objArr, Codec<T> elementCodec, Predicate<T> writeElementIf) {
        CompoundTag tableTag = new CompoundTag();

        for (int i = 0; i < objArr.length; i++) {
            CompoundTag column = new CompoundTag();

            for (int j = 0; j < objArr[i].length; j++) {
                if (writeElementIf.test(objArr[i][j])) {
                    Tag elementTag = encodeStrictly(elementCodec, NBT_OPS, objArr[i][j]);
                    column.put(Integer.toString(j), elementTag);
                }
            }

            column.putInt("size", objArr[i].length);

            tableTag.put(Integer.toString(i), column);
        }

        tableTag.putInt("size", objArr.length);

        return tableTag;
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException() {
        }

        public NotFoundException(String message) {
            super(message);
        }

        public NotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public NotFoundException(Throwable cause) {
            super(cause);
        }

        public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
