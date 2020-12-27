package ru.timeconqueror.timecore.api.registry.util;

import net.minecraft.block.AbstractBlock;
import net.minecraft.item.Item;

import java.util.function.Supplier;

/**
 * Factory for creating properties with applied configurations, represented in {@link #creator}.
 */
public class BlockPropsFactory {
    private final Supplier<AbstractBlock.Properties> creator;

    public BlockPropsFactory(Supplier<AbstractBlock.Properties> creator) {
        this.creator = creator;
    }

    /**
     * Creates new {@link Item.Properties} object, that must be provided by {@link #creator}.
     */
    public AbstractBlock.Properties create() {
        return creator.get();
    }

    public static AbstractBlock.Properties unbreakable(AbstractBlock.Properties props) {
        return props.strength(-1.0F, Float.MAX_VALUE);
    }
}
