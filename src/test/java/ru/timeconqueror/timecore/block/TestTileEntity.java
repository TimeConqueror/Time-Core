package ru.timeconqueror.timecore.block;

import net.minecraft.tileentity.TileEntity;
import ru.timeconqueror.timecore.registry.TTileEntities;

public class TestTileEntity extends TileEntity {
    public TestTileEntity() {
        super(TTileEntities.TEST_TE_TYPE);
    }
}
