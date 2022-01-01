package ru.timeconqueror.timecore.block.tile;

import net.minecraft.world.level.block.entity.BlockEntity;
import ru.timeconqueror.timecore.registry_example.deferred.TileEntityDeferredRegistryExample;

public class DummyTileEntity extends BlockEntity {
    public DummyTileEntity() {
        super(TileEntityDeferredRegistryExample.TEST_TE_TYPE.get());

        System.out.println("Me placed");

        System.out.println(getType());
    }
}
