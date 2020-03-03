package ru.timeconqueror.timecore.api.registry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.function.Consumer;

/**
 * Factory for creating properties with applied configurations, represented in {@link #processor}.
 */
public class ItemPropertiesFactory {
    private Consumer<Item.Properties> processor;

    /**
     * @param group Item Group that will be applied for every properties object, created via {{@link #createProps()}}.
     */
    public ItemPropertiesFactory(ItemGroup group) {
        this(props -> props.group(group));
    }

    /**
     * @param processor used to apply your options, like adding max damage for every properties object, created via {{@link #createProps()}}.
     */
    public ItemPropertiesFactory(Consumer<Item.Properties> processor) {
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
