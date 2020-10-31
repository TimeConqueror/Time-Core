package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.StandardItemModelParents;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.ModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.util.EnvironmentUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemRegister extends ForgeRegister<Item> {
    public ItemRegister(String modid) {
        super(ForgeRegistries.ITEMS, modid);
    }

    public <I extends Item> ItemRegisterChain<I> register(String name, Supplier<I> entrySup) {
        RegistryObject<I> holder = registerEntry(name, entrySup);

        return new ItemRegisterChain<>(holder);
    }

    public class ItemRegisterChain<I extends Item> extends ForgeRegister.RegisterChain<I> {
        public ItemRegisterChain(RegistryObject<I> holder) {
            super(ItemRegister.this, holder);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/generated" parent model.
         */
        public ItemRegisterChain<I> genDefaultModel(TextureLocation texture) {
            return genModel(StandardItemModelParents.DEFAULT, texture);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/handheld" parent model.
         */
        public ItemRegisterChain<I> genHandheldModel(TextureLocation texture) {
            return genModel(StandardItemModelParents.HANDHELD, texture);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with dependency on block model.
         */
        public ItemRegisterChain<I> genModelFromBlockParent(BlockModelLocation parentBlockModelLocation) {
            return genModel(() -> new ItemModel(parentBlockModelLocation));
        }

        /**
         * Creates and registers simple multilayer item model without the need of json file (via code) for bound item with provided standard parent model.
         *
         * @param textureLayers Commonly you will need to provide only one texture to the model,
         *                      but sometimes you will need to set model to use combination of several textures.
         *                      Vanilla uses it in, for example, spawn egg model where the layers are represented by base texture and overlay (spots).
         */
        public ItemRegisterChain<I> genModel(StandardItemModelParents parent, TextureLocation... textureLayers) {
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
        public ItemRegisterChain<I> genModel(ModelLocation parent, TextureLocation... textureLayers) {
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
        public ItemRegisterChain<I> genModel(Supplier<ItemModel> itemModelSupplier) {
            runOnlyForClient(() -> TimeClient.RESOURCE_HOLDER.addItemModel(asRegistryObject().get(), itemModelSupplier.get()));
            return this;
        }

        /**
         * Adds item entry to {@link LangGeneratorFacade}, which will place all entries in en_us.json file upon {@link GatherDataEvent}.
         * Generator will generate entries only in {@code runData} launch mode.
         *
         * @param enName english localization location of item
         */
        public ItemRegisterChain<I> genLangEntry(String enName) {
            if (EnvironmentUtils.isInDev()) {
                runTaskAfterRegistering(() -> LangGeneratorFacade.addItemEntry(asRegistryObject().get(), enName));
            }
            return this;
        }

        /**
         * Adds item entry to {@link LangGeneratorFacade}, which will place all entries in en_us.json file upon {@link GatherDataEvent}.
         * Generator will generate entries only in {@code runData} launch mode.<br>
         * <p>
         * This method is only for common armor stuff names, like the "Diamond Helmet", where equipment slot ("Helmet") is the last word.<br>
         * The last word, which represents the equipment slot, will be framed automatically.<br>
         * <p>
         * For uncommon names see {@link #genArmorLangEntry(String)} to set location directly.
         *
         * @param materialEnName the english location of material, will be the first word in the full location
         * @throws IllegalArgumentException if it is called for Items, that don't extend {@link ArmorItem}
         */
        public ItemRegisterChain<I> genArmorLangEntryByMaterial(String materialEnName) {
            if (EnvironmentUtils.isInDev()) {
                runTaskAfterRegistering(() -> {
                    Item item = asRegistryObject().get();
                    if (item instanceof ArmorItem) {
                        LangGeneratorFacade.addArmorEntryByMaterial((ArmorItem) item, materialEnName);
                    } else {
                        throw new IllegalArgumentException("#genArmorLangEntry will only work with armor items. For common items use #genLangEntry instead.");
                    }
                });
            }
            return this;
        }

        /**
         * Adds item entry to {@link LangGeneratorFacade}, which will place all entries in en_us.json file upon {@link GatherDataEvent}.
         * Generator will generate entries only in {@code runData} launch mode.<br>
         * <p>
         * This method is only for uncommon armor stuff names, like the "Helmet of The Dark One", where "Helmet" is the first word, not the last.<br>
         * <p>
         * For common names see {@link #genArmorLangEntryByMaterial(String)}
         *
         * @param enName english localization location of item
         * @throws IllegalArgumentException if it is called for Items, that don't extend {@link ArmorItem}
         */
        public ItemRegisterChain<I> genArmorLangEntry(String enName) {
            runTaskAfterRegistering(() -> {
                Item item = asRegistryObject().get();
                if (item instanceof ArmorItem) {
                    LangGeneratorFacade.addArmorEntry((ArmorItem) item, enName);
                } else {
                    throw new IllegalArgumentException("#genArmorLangEntry will only work with armor items. For common items use #genLangEntry instead.");
                }
            });
            return this;
        }

        /**
         * Runs task for current registrator directly after registering object.
         * Entry for {@link #asRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public ItemRegisterChain<I> addActionAfterRegistering(Consumer<ItemRegisterChain<I>> task) {
            runTaskAfterRegistering(() -> task.accept(this));
            return this;
        }

        /**
         * Runs task for current registrator  on client setup.
         * Entry for {@link #asRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public ItemRegisterChain<I> addActionOnClientSetup(Consumer<ItemRegisterChain<I>> task) {
            runTaskOnClientSetup(() -> task.accept(this));
            return this;
        }
    }
}
