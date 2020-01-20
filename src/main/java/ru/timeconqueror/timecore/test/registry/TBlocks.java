package ru.timeconqueror.timecore.test.registry;

import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.common.registry.block.BlockTimeRegistry;

@TimeAutoRegistry
public class TBlocks extends BlockTimeRegistry {
//    Block mcSand = new Block();

    public TBlocks() {
        super(TimeCore.INSTANCE);
    }

    @Override
    public void register() {

    }
}
