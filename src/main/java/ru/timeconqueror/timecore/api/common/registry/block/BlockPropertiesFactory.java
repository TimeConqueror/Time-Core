package ru.timeconqueror.timecore.api.common.registry.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.function.Supplier;

/**
 * Factory for creating properties with applied configurations, represented in {@link #creator}.
 */
public class BlockPropertiesFactory {
    /**
     * Used to create <b>NEW AND ONLY NEW</b> instances of {@link Block.Properties}.
     */
    private Supplier<Block.Properties> creator;

    public BlockPropertiesFactory(Supplier<Block.Properties> creator) {
        this.creator = creator;
    }

    /**
     * Creates new {@link Item.Properties} object, that must be provided by {@link #creator}.
     */
    public Block.Properties createProps() {
        return creator.get();
    }
}
