package ru.timeconqueror.timecore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import ru.timeconqueror.timecore.registry_example.deferred.TileEntityDeferredRegistryExample;

import javax.annotation.Nullable;

public class DummyBlockWithTileEntity extends Block {
    public DummyBlockWithTileEntity(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityDeferredRegistryExample.TEST_TE_TYPE.get().create();
    }
}
