package ru.timeconqueror.timecore.animation_example.block_example.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import ru.timeconqueror.timecore.animation_example.block_example.registry.ATileRegistry;

import javax.annotation.Nullable;

public class HeatCubeBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);

    public HeatCubeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ATileRegistry.HEAT_CUBE.create();
    }

//    @Override
//    public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
//        return super.isNormalCube(p_220081_1_, p_220081_2_, p_220081_3_);
//    }
}
