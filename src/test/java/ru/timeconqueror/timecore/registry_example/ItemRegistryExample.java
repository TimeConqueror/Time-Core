package ru.timeconqueror.timecore.registry_example;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.ItemRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.ItemPropsFactory;
import ru.timeconqueror.timecore.api.util.Hacks;

@AutoRegistrable.Entries("item")
public class ItemRegistryExample {
    public static Item TEST_DIAMOND = Hacks.promise();

    private static class Init {
        @AutoRegistrable
        private static final ItemRegister REGISTER = new ItemRegister(TimeCore.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            ItemPropsFactory miscGrouped = new ItemPropsFactory(ItemGroup.TAB_MISC);

            REGISTER.register("test_diamond", () -> new Item(miscGrouped.create()))
                    .defaultModel(new TextureLocation("minecraft", "item/diamond"));
        }
    }
}
