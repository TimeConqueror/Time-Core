package examples.block;

import com.mojang.serialization.MapCodec;
import examples.animation_example.block_example.block.HeatCubeBlock;
import examples.registry_example.deferred.BlockEntityDeferredRegistryExample;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class DummyBlockWithEntity extends BaseEntityBlock {
    public static final MapCodec<DummyBlockWithEntity> CODEC = simpleCodec(DummyBlockWithEntity::new);

    public DummyBlockWithEntity(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos_, BlockState state_) {
        return BlockEntityDeferredRegistryExample.TEST_TE_TYPE.get().create(pos_, state_);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
