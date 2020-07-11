package ru.timeconqueror.timecore.block.tile;

import net.minecraft.tileentity.TileEntity;
import ru.timeconqueror.timecore.registry.deferred.TileEntityDeferredRegistryExample;

public class DummyTileEntity extends TileEntity {
    public DummyTileEntity() {
        super(TileEntityDeferredRegistryExample.TEST_TE_TYPE.get());

        System.out.println("Me placed");

        System.out.println(getType());
    }
}
