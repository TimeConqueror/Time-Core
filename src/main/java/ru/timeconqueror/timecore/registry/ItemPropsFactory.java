package ru.timeconqueror.timecore.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.function.Consumer;

/**
 * Factory for creating properties with applied configurations, represented in {@link #processor}.
 */
public class ItemPropsFactory {
    private final Consumer<Item.Properties> processor;

    /**
     * @param group Item Group that will be applied for every properties object, created via {{@link #createProps()}}.
     */
    public ItemPropsFactory(ItemGroup group) {
        this(props -> props.tab(group));
    }

    /**
     * @param processor used to apply your options, like adding max damage for every properties object, created via {{@link #createProps()}}.
     */
    public ItemPropsFactory(Consumer<Item.Properties> processor) {
        this.processor = processor;
    }

    /**
     * Creates new {@link Item.Properties} object with applied {@link #processor} configurations.
     */
    public Item.Properties createProps() {
        Item.Properties props = new Item.Properties();
        processor.accept(props);

        return props;
    }
}
