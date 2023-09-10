package ru.timeconqueror.timecore.common.registry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.network.codec.BlockEntityCodec;
import ru.timeconqueror.timecore.animation.network.codec.EntityCodec;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.registry.SimpleVanillaRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LevelObjectCodecs {
    public static final LevelObjectCodec.Factory<Entity> ENTITY = new LevelObjectCodec.Factory<>() {
        @Override
        public EntityCodec create(Entity object) {
            return new EntityCodec(this, object);
        }

        @Override
        public EntityCodec create(FriendlyByteBuf buffer) {
            return new EntityCodec(this, buffer);
        }
    };
    public static final LevelObjectCodec.Factory<BlockEntity> BLOCK_ENTITY = new LevelObjectCodec.Factory<>() {
        @Override
        public BlockEntityCodec create(BlockEntity object) {
            return new BlockEntityCodec(this, object);
        }

        @Override
        public BlockEntityCodec create(FriendlyByteBuf buffer) {
            return new BlockEntityCodec(this, buffer);
        }
    };

    @AutoRegistrable
    private static final SimpleVanillaRegister<LevelObjectCodec.Factory<?>> REGISTER = new SimpleVanillaRegister<>(TCRegistries.ANIMATION_NETWORK_DISPATCHER_REGISTRY, TimeCore.MODID);

    @AutoRegistrable.Init
    public static void register() {
        REGISTER.register("entity", () -> ENTITY);
        REGISTER.register("tile_entity", () -> BLOCK_ENTITY);
    }
}
