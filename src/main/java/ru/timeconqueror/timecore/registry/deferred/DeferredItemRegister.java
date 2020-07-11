package ru.timeconqueror.timecore.registry.deferred;

import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.StandardItemModelParents;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.ModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.deferred.base.DeferredFMLImplForgeRegister;

import java.util.function.Supplier;

/**
 * Deferred Register for items.
 * <p>
 * To work it needs to be in a static field in registry class and be annotated with {@link TimeAutoRegistrable}.
 * Extra params in this annotation are ignored.
 */
public class DeferredItemRegister extends DeferredFMLImplForgeRegister<Item> {
    public DeferredItemRegister(String modid) {
        super(ForgeRegistries.ITEMS, modid);
    }

    public ItemRegistrator regItem(String name, Supplier<? extends Item> sup) {
        return new ItemRegistrator(name, sup);
    }

    public class ItemRegistrator extends Registrator {
        protected ItemRegistrator(String name, Supplier<? extends Item> sup) {
            super(name, sup);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/generated" parent model.
         */
        public ItemRegistrator genDefaultModel(TextureLocation texture) {
            return genModel(StandardItemModelParents.DEFAULT, texture);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/handheld" parent model.
         */
        public ItemRegistrator genHandheldModel(TextureLocation texture) {
            return genModel(StandardItemModelParents.HANDHELD, texture);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with dependency on block model.
         */
        public ItemRegistrator genModelFromBlockParent(BlockModelLocation parentBlockModelLocation) {
            return genModel(() -> new ItemModel(parentBlockModelLocation));
        }

        /**
         * Creates and registers simple multilayer item model without the need of json file (via code) for bound item with provided standard parent model.
         *
         * @param textureLayers Commonly you will need to provide only one texture to the model,
         *                      but sometimes you will need to set model to use combination of several textures.
         *                      Vanilla uses it in, for example, spawn egg model where the layers are represented by base texture and overlay (spots).
         */
        public ItemRegistrator genModel(StandardItemModelParents parent, TextureLocation... textureLayers) {
            return genModel(parent.getModelLocation(), textureLayers);
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
        public ItemRegistrator genModel(ModelLocation parent, TextureLocation... textureLayers) {
            return genModel(() -> {
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
        public ItemRegistrator genModel(Supplier<ItemModel> itemModelSupplier) {
            runOnlyForClient(() -> TimeClient.RESOURCE_HOLDER.addItemModel(getRegistryObject().get(), itemModelSupplier.get()));
            return this;
        }
    }
}
