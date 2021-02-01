package ru.timeconqueror.timecore.api.registry.util;

import net.minecraft.block.AbstractBlock;
import net.minecraft.item.Item;

import java.util.function.Supplier;

/**
 * Factory for creating properties with applied configurations, represented in {@link #factory}.
 */
public class BlockPropsFactory {
    private final Supplier<AbstractBlock.Properties> factory;

    public BlockPropsFactory(Supplier<AbstractBlock.Properties> factory) {
        this.factory = factory;
    }

    /**
     * Creates new {@link Item.Properties} object, that must be provided by {@link #factory}.
     */
    public AbstractBlock.Properties create() {
        return factory.get();
    }

    public static AbstractBlock.Properties unbreakable(AbstractBlock.Properties props) {
        return props.strength(-1.0F, Float.MAX_VALUE);
    }

    public static BlockPropsFactory of(Supplier<AbstractBlock.Properties> factory) {
        return new BlockPropsFactory(factory);
    }
}
