package ru.timeconqueror.timecore.common.registry.item;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.timeconqueror.timecore.api.ITimeMod;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.ModelItem;
import ru.timeconqueror.timecore.api.client.resource.StandardItemModelParents;
import ru.timeconqueror.timecore.api.client.resource.location.ModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.common.registry.ForgeTimeRegistry;

public abstract class ItemTimeRegistry extends ForgeTimeRegistry<Item> {

    public ItemTimeRegistry(ITimeMod mod) {
        super(mod);
    }

    @SubscribeEvent
    public final void onRegItemsEvent(RegistryEvent.Register<Item> event) {
        onFireRegistryEvent(event);
    }

    /**
     * Method to automatically register provided item.
     *
     * @param item item to register
     * @param name item name. Will be used as a part of registry and translation keys. Should NOT contain mod ID, because it will be bound internally.
     * @return {@link ItemWrapper} to provide extra register options.
     */
    public ItemWrapper regItem(Item item, String name) {
        return new ItemWrapper(item, name);
    }

    /**
     * Used to set extra options for given item.
     * <p>
     * All methods represented here are optional.
     */
    public class ItemWrapper extends EntryWrapper {
        public ItemWrapper(Item entry, String name) {
            super(entry, name);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/generated" parent model.
         */
        public ItemWrapper regDefaultModel(TextureLocation texture) {
            return regModel(StandardItemModelParents.DEFAULT, texture);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/handheld" parent model.
         */
        public ItemWrapper regHandheldModel(TextureLocation texture) {
            return regModel(StandardItemModelParents.HANDHELD, texture);
        }

        /**
         * Creates and registers simple multilayer item model without the need of json file (via code) for bound item with provided standard parent model.
         *
         * @param textureLayers Commonly you will need to provide only one texture to the model,
         *                      but sometimes you will need to set model to use combination of several textures.
         *                      Vanilla uses it in, for example, spawn egg model where the layers are represented by base texture and overlay (spots).
         */
        public ItemWrapper regModel(StandardItemModelParents parent, TextureLocation... textureLayers) {
            return regModel(parent.getModelLocation(), textureLayers);
        }

        /**
         * Creates and registers simple multilayer item model without the need of json file (via code) for bound item with provided parent model resource location.
         *
         * @param parent        parent model resource location.
         *                      You can provide its path with or without <b>{@code 'models/'}</b> part.
         * @param textureLayers Commonly you will need to provide only one texture to the model,
         *                      but sometimes you will need to set model to use combination of several textures.
         *                      Vanilla uses it in, for example, spawn egg model where the layers are represented by base texture and overlay (spots).
         */
        public ItemWrapper regModel(ModelLocation parent, TextureLocation... textureLayers) {
            ModelItem model = new ModelItem(parent);
            model.addTextureLayers(textureLayers);

            return regModel(model);
        }

        /**
         * Registers simple item model without the need of json file (via code) for bound item.
         */
        public ItemWrapper regModel(ModelItem model) {
            TimeClient.RESOURCE_HOLDER.addItemModel(getEntry(), model);
            return this;
        }
    }
}
