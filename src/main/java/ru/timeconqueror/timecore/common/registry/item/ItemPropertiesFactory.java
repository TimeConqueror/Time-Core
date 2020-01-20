package ru.timeconqueror.timecore.common.registry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.function.Consumer;

public class ItemPropertiesFactory {
    private Consumer<Item.Properties> processor;

    public ItemPropertiesFactory(ItemGroup group) {
        this(props -> props.group(group));
    }

    public ItemPropertiesFactory(Consumer<Item.Properties> processor) {
        this.processor = processor;
    }

    public Item.Properties createProps() {
        Item.Properties props = new Item.Properties();
        processor.accept(props);

        return props;
    }
}
