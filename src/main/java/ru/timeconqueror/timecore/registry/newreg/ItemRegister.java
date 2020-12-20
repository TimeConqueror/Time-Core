package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.StandardItemModelParents;
import ru.timeconqueror.timecore.api.client.resource.TimeResourceHolder;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.ModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.storage.LoadingOnlyStorage;
import ru.timeconqueror.timecore.util.EnvironmentUtils;
import ru.timeconqueror.timecore.util.Hacks;
import ru.timeconqueror.timecore.util.Temporal;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * All {@link TimeRegister}s are used to simplify stuff registering.
 * You can use it for both {@link RegistryObject} or {@link ObjectHolder} style.
 * <p>
 * To use it you need to:
 * <ol>
 *     <li>Create its instance and declare it static. Access modifier can be any.</li>
 *     <li>Attach {@link AutoRegistrable} annotation to it to register it as an event listener.</li>
 *     <li>Extend you main mod class from {@link TimeMod} to enable TimeCore's annotations.</li>
 * </ol>
 *
 * <b>Features:</b>
 * If you need to register stuff, your first step will be to call method #register.
 * If the register system has any extra available registering stuff, then this method will return Register Chain,
 * which will have extra methods to apply.
 * Otherwise it will RegistryObject, which can be used or not used (depending on your registry style).
 * <br>
 * <br>
 * <b>{@link RegistryObject} style:</b>
 * <br>
 * <blockquote>
 *     <pre>
 *     public class TileEntityDeferredRegistryExample {
 *         {@literal @}AutoRegistrable
 *          private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);
 *
 *          public static RegistryObject<TileEntityType<DummyTileEntity>> TEST_TE_TYPE = REGISTER.register("test_tile", DummyTileEntity::new, BlockRegistryExample.TEST_BLOCK_WITH_TILE)
 *              .regCustomRenderer(() -> DummyTileEntityRenderer::new) // <- one of extra features
 *              .asRegistryObject(); // <- retrieving registry object from our register chain.
 *      }
 *     </pre>
 * </blockquote>
 * <br>
 * <b>{@link ObjectHolder} style:</b>
 * <br>
 * For this style you need to know one thing:
 * you will need two classes: one for storing registry values and one for registering them.
 * In the following case I made registering class an inner class of storing class.
 * If you want, you may store them in separate files, there's no matter.
 * <p>
 * So the storing (main) class needs to have {@link ObjectHolder} annotation with your mod id to inject values in all public static final fields.
 * The name of the field should match it's registry name (ignoring case).
 * More about it you can check in (<a href=https://mcforge.readthedocs.io/en/1.16.x/>Forge Documentation</a>)
 * <p>
 * The inner class will be used for us as a registrator. It should be static, but can have any access modifier.
 * We still add {@link TimeRegister} there as stated above. (with AutoRegistrable annotation, etc.)]
 * <p>
 * One more thing: we should add is a <b>static</b> register method and annotate with {@link AutoRegistrable.InitMethod}. Method can have any access modifier.
 * There we will register all needed stuff, using {@link TimeRegister} field.
 * Method annotated with {@link AutoRegistrable.InitMethod} can have zero parameters or one {@link FMLConstructModEvent} parameter.
 * It will be called before Registry events to prepare all the stuff.
 * <p>
 * As you can see, I used {@link Hacks#promise()} method for public static final fields that will be initialized later.
 * You can place there null, but some IDE may always tell you, that it expects the NullPointerException in all places, where you call it.
 * We know, that it will be initialized later, so using {@link Hacks#promise()} we set null in this field, but disables IDE null checks for it.
 *
 * <br>
 * <blockquote>
 *     <pre>
 *     {@literal @}ObjectHolder(TimeCore.MODID)
 *      public class ItemRegistryExample {
 *          public static final Item TEST_DIAMOND = Hacks.promise();
 *
 *          private static class Init {
 *             {@literal @}AutoRegistrable
 *              private static final ItemRegister REGISTER = new ItemRegister(TimeCore.MODID);
 *
 *             {@literal @}AutoRegistrable.InitMethod
 *              private static void register() {
 *                  ItemPropsFactory miscGrouped = new ItemPropsFactory(ItemGroup.TAB_MISC);
 *
 *                  REGISTER.register("test_diamond", () -> new Item(miscGrouped.create()))
 *                          .genDefaultModel(new TextureLocation("minecraft", "item/diamond"));
 *               }
 *          }
 *      }
 *     </pre>
 * </blockquote>
 * <p>
 * <p>
 * Examples can be seen at test module.
 */
public class ItemRegister extends ForgeRegister<Item> {
    private final Temporal<TimeResourceHolder> resourceHolder = Temporal.of(new TimeResourceHolder(), "Called too late. Resources were already loaded.");

    public ItemRegister(String modid) {
        super(ForgeRegistries.ITEMS, modid);
    }

    /**
     * Adds entry in provided {@code entrySup} to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link ItemRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All method of {@link ItemRegisterChain} are optional.
     *
     * @param name     The item's name, will automatically have the modid as a namespace.
     * @param entrySup A factory for the new item, it should return a new instance every time it is called.
     * @return A {@link ItemRegisterChain} for adding some extra stuff.
     * @see ItemRegisterChain
     */
    public <I extends Item> ItemRegisterChain<I> register(String name, Supplier<I> entrySup) {
        RegistryObject<I> holder = registerEntry(name, entrySup);

        return new ItemRegisterChain<>(holder);
    }

    @Override
    protected void onRegEvent(RegistryEvent.Register<Item> event) {
        super.onRegEvent(event);

        LoadingOnlyStorage.addResourceHolder(resourceHolder.remove());
    }

    public class ItemRegisterChain<I extends Item> extends ForgeRegister.RegisterChain<I> {
        private ItemRegisterChain(RegistryObject<I> holder) {
            super(holder);
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/generated" parent model.
         * Will require the texture with path: "${your_modid}:textures/item/${registry_name}.png"
         * Example: "my_mod:textures/item/sparkle.png"
         *
         * @param parentType type of generated model: generated or handheld
         */
        public ItemRegisterChain<I> genModel(StandardItemModelParents parentType) {
            clientSideOnly(() -> genModel(parentType, new TextureLocation(getModId(), "item/" + getName())));
            return this;
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/generated" parent model.
         */
        public ItemRegisterChain<I> genDefaultModel(TextureLocation texture) {
            clientSideOnly(() -> genModel(StandardItemModelParents.DEFAULT, texture));
            return this;
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with one provided texture and "item/handheld" parent model.
         */
        public ItemRegisterChain<I> genHandheldModel(TextureLocation texture) {
            clientSideOnly(() -> genModel(StandardItemModelParents.HANDHELD, texture));
            return this;
        }

        /**
         * Creates and registers simple item model without the need of json file (via code) for bound item with dependency on block model.
         */
        public ItemRegisterChain<I> genModelFromBlockParent(BlockModelLocation parentBlockModelLocation) {
            clientSideOnly(() -> genModel(new ItemModel(parentBlockModelLocation)));
            return this;
        }

        /**
         * Creates and registers simple multilayer item model without the need of json file (via code) for bound item with provided standard parent model.
         *
         * @param textureLayers Commonly you will need to provide only one texture to the model,
         *                      but sometimes you will need to set model to use combination of several textures.
         *                      Vanilla uses it in, for example, spawn egg model where the layers are represented by base texture and overlay (spots).
         */
        public ItemRegisterChain<I> genModel(StandardItemModelParents parent, TextureLocation... textureLayers) {
            clientSideOnly(() -> genModel(parent.getModelLocation(), textureLayers));
            return this;
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
            clientSideOnly(() -> genModel(new ItemModel(parent).addTextureLayers(textureLayers)));

            return this;
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
            clientSideOnly(() -> genModel(itemModelSupplier.get()));
            return this;
        }

        /**
         * Registers simple item model without the need of json file (via code) for bound item.
         *
         * @param itemModel model for this item.
         *                  For details see {@link ItemModel}.
         */
        public ItemRegisterChain<I> genModel(ItemModel itemModel) {
            clientSideOnly(() -> resourceHolder.get().addItemModel(getRegistryName(), itemModel));
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
                runTaskAfterRegistering(() -> ItemRegister.this.getLangGeneratorFacade().addItemEntry(asRegistryObject().get(), enName));
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
                        ItemRegister.this.getLangGeneratorFacade().addArmorEntryByMaterial((ArmorItem) item, materialEnName);
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
                    ItemRegister.this.getLangGeneratorFacade().addArmorEntry((ArmorItem) item, enName);
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
        public ItemRegisterChain<I> doAfterRegister(Consumer<ItemRegisterChain<I>> task) {
            runTaskAfterRegistering(() -> task.accept(this));
            return this;
        }

        /**
         * Runs task for current registrator  on client setup.
         * Entry for {@link #asRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public ItemRegisterChain<I> doOnClientSetup(Consumer<ItemRegisterChain<I>> task) {
            runTaskOnClientSetup(() -> task.accept(this));
            return this;
        }
    }
}
