package ru.timeconqueror.timecore.common.registry.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.timeconqueror.timecore.api.ITimeMod;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.StandardItemModelParents;
import ru.timeconqueror.timecore.common.registry.TimeForgeRegistry;

public abstract class TimeItemRegistry extends TimeForgeRegistry<Item> {

    public TimeItemRegistry(ITimeMod mod) {
        super(mod);
    }

    @SubscribeEvent
    public void onRegItemsEvent(RegistryEvent.Register<Item> event) {
        onFireRegistryEvent(event);
    }

    @Override
    public void onFireRegistryEvent(RegistryEvent.Register<Item> event) {
        super.onFireRegistryEvent(event);
    }

    public ItemWrapper regItem(Item item, String name) {
        return new ItemWrapper(item, name);
    }

    public class ItemWrapper extends EntryWrapper {
        public ItemWrapper(Item entry, String name) {
            super(entry, name);
        }

        public ItemWrapper regDefaultModel(ResourceLocation texture) {
            return regModel(StandardItemModelParents.DEFAULT, texture);
        }

        public ItemWrapper regHandheldModel(ResourceLocation texture) {
            return regModel(StandardItemModelParents.HANDHELD, texture);
        }

        public ItemWrapper regModel(StandardItemModelParents parent, ResourceLocation... textureLayers) {
            return regModel(parent.getResourceLocation(), textureLayers);
        }

        public ItemWrapper regModel(ResourceLocation parent, ResourceLocation... textureLayers) {
            ItemModel model = new ItemModel(parent);
            model.addTextureLayers(textureLayers);

            return regModel(model);
        }

        public ItemWrapper regModel(ItemModel model) {
            TimeClient.RESOURCE_HOLDER.addItemModel(get(), model);
            return this;
        }
    }
}
