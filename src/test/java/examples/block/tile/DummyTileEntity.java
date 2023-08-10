package examples.block.tile;

import examples.registry_example.deferred.TileEntityDeferredRegistryExample;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DummyTileEntity extends BlockEntity {
    public DummyTileEntity(BlockPos pos, BlockState state) {
        super(TileEntityDeferredRegistryExample.TEST_TE_TYPE.get(), pos, state);

        System.out.println("Me placed");

        System.out.println(getType());
    }
}
