package examples.registry_example;

import net.minecraft.world.item.Item;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.ItemRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.util.Hacks;

@AutoRegistrable.Entries("item")
public class ItemRegistryExample {
    public static Item TEST_DIAMOND = Hacks.promise();

    private static class Init {
        @AutoRegistrable
        private static final ItemRegister REGISTER = new ItemRegister(TimeCore.MODID);

        @AutoRegistrable.Init
        private static void register() {
            REGISTER.register("test_diamond", () -> new Item(new Item.Properties()))
                    .defaultModel(new TextureLocation("minecraft", "item/diamond"));
        }
    }
}
