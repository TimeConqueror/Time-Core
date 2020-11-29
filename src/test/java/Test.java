import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class Test {
    public static Codec<Simple> SIMPLE_CODEC = Codec.INT.xmap(Simple::new, Simple::getId);

    public static Codec<MSField> FIELD_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("bool").forGetter(MSField::isBool),
            SIMPLE_CODEC.fieldOf("simple").forGetter(MSField::getSimple)
    ).apply(instance, MSField::new));

    public static void main(String[] args) throws CommandSyntaxException {
//        INBT encode = encode(FIELD_CODEC, NBT_OPS, new MSField(true, null));

//        INBT encode = new JsonToNBT(new StringReader("{bool:1b}")).readValue();
//        System.out.println(encode);
//
//        MSField decode = decode(FIELD_CODEC, NBT_OPS, encode);
//        System.out.println(decode);

//        Either<String, String> typedTileEntity = getTypedTileEntity(null, null);
//        System.out.println(typedTileEntity.left().get().charAt(0));

//        new EnumCodec<>(Type.class);
    }

    public enum Type {
        ANY("any"),
        WIN("win"),
        LOSE("lose");

        private final String name;

        Type(String name) {
            this.name = name;
        }
    }

    public static <T> Either<T, String> getTypedTileEntity(World world, BlockPos pos) {
        try {
            return Either.left((T) new Object());
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static class MSField {
        private final boolean bool;
        private final Simple simple;

        public MSField(boolean bool, Simple simple) {
            this.bool = bool;
            this.simple = simple;
        }

        public Simple getSimple() {
            return simple;
        }

        public boolean isBool() {
            return bool;
        }

        @Override
        public String toString() {
            return "MSField{" +
                    "bool=" + bool +
                    ", simple=" + simple +
                    '}';
        }
    }

    private static class Simple {
        private final int id;

        public Simple(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Simple{" +
                    "id=" + id +
                    '}';
        }
    }

    public static final NBTDynamicOps NBT_OPS = NBTDynamicOps.INSTANCE;
    public static final JsonOps JSON_OPS = JsonOps.INSTANCE;

    public static <T, SERIALIZED> T decode(Codec<T> codec, DynamicOps<SERIALIZED> ops, SERIALIZED input, T defaultVal) {
        return codec.decode(ops, input).result().map(Pair::getFirst).orElse(defaultVal);
    }

    public static <T, SERIALIZED> T decode(Codec<T> codec, DynamicOps<SERIALIZED> ops, SERIALIZED input) {
        return codec.decode(ops, input).result().map(Pair::getFirst).orElseThrow(NotFoundException::new);
    }

    public static <T, SERIALIZED> void encode(Codec<T> codec, DynamicOps<SERIALIZED> ops, T input, Consumer<SERIALIZED> actionIfProvided) {
        codec.encodeStart(ops, input).result().ifPresent(actionIfProvided);
    }

    public static <T, SERIALIZED> SERIALIZED encode(Codec<T> codec, DynamicOps<SERIALIZED> ops, T input) {
        return codec.encodeStart(ops, input).result().orElseThrow(NotFoundException::new);
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
