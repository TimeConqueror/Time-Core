package ru.timeconqueror.timecore.api.registry.item;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.StandardItemModelParents;
import ru.timeconqueror.timecore.api.client.resource.location.ModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.api.registry.WrappedForgeTimeRegistry;

import java.util.function.Supplier;

/**
 * Used for easy item registering.
 * Any class that extends this, should be extended and annotated with {@link TimeAutoRegistry}.
 * <p>
 * Examples can be seen at test module.
 */
public abstract class ItemTimeRegistry extends WrappedForgeTimeRegistry<Item> {

    public ItemTimeRegistry(TimeMod mod) {
        super(mod);
    }

    @SubscribeEvent
    public final void onRegItemsEvent(RegistryEvent.Register<Item> event) {
        onFireRegistryEvent(event);
    }

    /**
     * Method to register provided item automatically.
     *
     * @param item item to register
     * @param name item name. Will be used as a part of registry and translation keys. Should NOT contain mod ID, because it will be bound automatically.
     * @return {@link ItemWrapper} to provide extra register options.
     */
    public ItemWrapper regItem(Item item, String name) {
        return new ItemWrapper(item, name);
    }

    /**
     * Wrapper class to register extra options, like models, blockstates and item blocks without need of json-files!
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
            return regModel(() -> {
                ItemModel model = new ItemModel(parent);
                model.addTextureLayers(textureLayers);
                return model;
            });
        }

        /**
         * Registers simple item model without the need of json file (via code) for bound item.
         *
         * @param itemModelSupplier supplier for item model you want to register.
         *                          Supplier is used here to call its content only for client side, so all stuff that is returned by it
         *                          likely should not be created outside lambda (except locations).
         *                          For details see {@link ItemModel}.
         */
        public ItemWrapper regModel(Supplier<ItemModel> itemModelSupplier) {
            runForClient(() -> TimeClient.RESOURCE_HOLDER.addItemModel(getItem(), itemModelSupplier.get()));
            return this;
        }

        /**
         * Returns item bound to wrapper.
         * Method duplicates {@link #getEntry()}, so it exists only for easier understanding.
         */
        public Item getItem() {
            return getEntry();
        }
    }
}
