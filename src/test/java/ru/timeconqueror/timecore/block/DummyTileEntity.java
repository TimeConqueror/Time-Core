package ru.timeconqueror.timecore.block;

import net.minecraft.tileentity.TileEntity;
import ru.timeconqueror.timecore.registry.TTileEntities;

public class DummyTileEntity extends TileEntity {
    public DummyTileEntity() {
        super(TTileEntities.TEST_TE_TYPE);
    }
}
