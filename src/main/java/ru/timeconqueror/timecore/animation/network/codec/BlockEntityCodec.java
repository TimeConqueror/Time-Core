package ru.timeconqueror.timecore.animation.network.codec;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityCodec extends LevelObjectCodec<BlockEntity> {
    private final BlockPos pos;

    public BlockEntityCodec(Factory<? extends BlockEntity> parent, BlockEntity blockEntity) {
        super(parent);
        this.pos = blockEntity.getBlockPos();
    }

    public BlockEntityCodec(Factory<? extends BlockEntity> parent, FriendlyByteBuf buffer) {
        super(parent);
        this.pos = buffer.readBlockPos();
    }

    @Override
    protected void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public BlockEntity construct(Level level) {
        return level.getBlockEntity(pos);
    }
}