package ru.timeconqueror.timecore.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.function.Supplier;

/**
 * Factory for creating properties with applied configurations, represented in {@link #creator}.
 */
public class BlockPropsFactory {
    private final Supplier<Block.Properties> creator;

    public BlockPropsFactory(Supplier<Block.Properties> creator) {
        this.creator = creator;
    }

    /**
     * Creates new {@link Item.Properties} object, that must be provided by {@link #creator}.
     */
    public Block.Properties create() {
        return creator.get();
    }
}
