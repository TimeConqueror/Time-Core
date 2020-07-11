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

    public static Block.Properties setHardness(Block.Properties props, float hardness) {
        props.hardness = hardness;
        return props;
    }

    public static Block.Properties setResistance(Block.Properties props, float resistance) {
        props.resistance = resistance;
        return props;
    }

    public static Block.Properties setUnbreakable(Block.Properties props) {
        return setHardness(props, -1);
    }

    public static Block.Properties setInexplosive(Block.Properties props) {
        return setResistance(props, 3600000F);
    }

    public static Block.Properties setUnbreakableAndInexplosive(Block.Properties props) {
        return setInexplosive(setUnbreakable(props));
    }

    /**
     * Creates new {@link Item.Properties} object, that must be provided by {@link #creator}.
     */
    public Block.Properties create() {
        return creator.get();
    }
}
