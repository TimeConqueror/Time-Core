package ru.timeconqueror.timecore.common.registry.block;

import net.minecraft.block.Block;

import java.util.function.Supplier;

public class BlockPropertiesFactory {
    /**
     * Used to create new instances of {@link Block.Properties}.
     */
    private Supplier<Block.Properties> creator;

    public BlockPropertiesFactory(Supplier<Block.Properties> creator) {
        this.creator = creator;
    }

    public Block.Properties createProps() {
        return creator.get();
    }


}
