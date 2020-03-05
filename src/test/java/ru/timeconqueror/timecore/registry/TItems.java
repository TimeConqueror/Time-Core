package ru.timeconqueror.timecore.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.api.registry.item.ItemPropsFactory;
import ru.timeconqueror.timecore.api.registry.item.ItemTimeRegistry;

@TimeAutoRegistry
public class TItems extends ItemTimeRegistry {
    public static ItemPropsFactory miscGrouped = new ItemPropsFactory(ItemGroup.MISC);

    public static Item mcDiamond = new Item(miscGrouped.createProps());

    public TItems() {
        super(TimeCore.INSTANCE);
    }

    @Override
    public void register() {
        regItem(mcDiamond, "test_diamond").regDefaultModel(new TextureLocation("minecraft", "item/diamond"));
    }
}
