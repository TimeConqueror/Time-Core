package ru.timeconqueror.timecore.test.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.common.registry.item.ItemPropertiesFactory;
import ru.timeconqueror.timecore.common.registry.item.TimeItemRegistry;

@TimeAutoRegistry
public class TItems extends TimeItemRegistry {
    public static ItemPropertiesFactory miscGrouped = new ItemPropertiesFactory(ItemGroup.MISC);

    public static Item mcDiamond = new Item(miscGrouped.createProps());

    public TItems() {
        super(TimeCore.INSTANCE);
    }

    @Override
    public void register() {
        regItem(mcDiamond, "test_diamond").regDefaultModel(new ResourceLocation("item/diamond"));
    }
}
