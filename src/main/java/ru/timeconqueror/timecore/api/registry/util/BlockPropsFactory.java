package ru.timeconqueror.timecore.api.registry.util;

import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * Factory for creating properties with applied configurations, represented in {@link #factory}.
 */
public class BlockPropsFactory {
    private final Supplier<BlockBehaviour.Properties> factory;

    public BlockPropsFactory(Supplier<BlockBehaviour.Properties> factory) {
        this.factory = factory;
    }

    /**
     * Creates new {@link BlockBehaviour.Properties} object, that must be provided by {@link #factory}.
     */
    public BlockBehaviour.Properties create() {
        return factory.get();
    }

    public static BlockBehaviour.Properties unbreakable(BlockBehaviour.Properties props) {
        return props.strength(-1.0F, Float.MAX_VALUE);
    }

    public static BlockPropsFactory of(Supplier<BlockBehaviour.Properties> factory) {
        return new BlockPropsFactory(factory);
    }
}
