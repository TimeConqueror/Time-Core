package ru.timeconqueror.timecore.api.registry;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource;
import ru.timeconqueror.timecore.api.client.resource.TimeResourceHolder;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.api.registry.ItemRegister.ItemRegisterChain;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.ItemPropsFactory;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;
import ru.timeconqueror.timecore.api.util.Hacks;
import ru.timeconqueror.timecore.api.util.Temporal;
import ru.timeconqueror.timecore.storage.LoadingOnlyStorage;

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
 *                          .defaultModel(new TextureLocation("minecraft", "item/diamond"));
 *               }
 *          }
 *      }
 *     </pre>
 * </blockquote>
 * <p>
 * <p>
 * Examples can be seen at test module.
 */
public class BlockRegister extends ForgeRegister<Block> {
    private final ItemRegister itemRegister;
    private final Temporal<TimeResourceHolder> resourceHolder = Temporal.of(new TimeResourceHolder(), "Called too late. Resources were already loaded.");

    public BlockRegister(String modid) {
        super(ForgeRegistries.BLOCKS, modid);
        itemRegister = new ItemRegister(modid);
    }

    /**
     * Adds entry in provided {@code entrySup} to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link BlockRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All method of {@link BlockRegisterChain} are optional.
     *
     * @param name     The block's name, will automatically have the modid as a namespace.
     * @param entrySup A factory for the new block, it should return a new instance every time it is called.
     * @return A {@link BlockRegisterChain} for adding some extra stuff.
     * @see BlockRegisterChain
     */
    public <B extends Block> BlockRegisterChain<B> register(String name, Supplier<B> entrySup) {
        RegistryObject<B> holder = registerEntry(name, entrySup);

        return new BlockRegisterChain<>(holder);
    }

    @Override
    protected void onRegEvent(RegistryEvent.Register<Block> event) {
        super.onRegEvent(event);

        LoadingOnlyStorage.addResourceHolder(resourceHolder.remove());
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        itemRegister.regToBus(modEventBus);
    }

    public class BlockRegisterChain<B extends Block> extends RegisterChain<B> {
        private BlockRegisterChain(RegistryObject<B> holder) {
            super(holder);
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry location as block has.
         * It will also generate default item model automatically based on the block one.
         *
         * @param group creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         */
        public BlockRegisterChain<B> defaultBlockItem(@Nullable CreativeModeTab group) {
            return defaultBlockItem(group, itemRegistrator -> itemRegistrator.modelFromBlockParent(new BlockModelLocation(getModId(), getName())));
        }

        /**
         * Sets render layer for this block.
         */
        public BlockRegisterChain<B> renderLayer(Supplier<RenderTypeWrapper> renderTypeSup) {
            runOnClientSetup(() -> ItemBlockRenderTypes.setRenderLayer(asRegistryObject().get(), renderTypeSup.get().get()));
            return this;
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry location as block has.
         * It will also generate default item model automatically based on the block one.
         *
         * @param group              creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         * @param blockModelLocation parent block model location for auto-generated item model based on block one.
         */
        public BlockRegisterChain<B> defaultBlockItem(@Nullable CreativeModeTab group, BlockModelLocation blockModelLocation) {
            return defaultBlockItem(group, itemRegistrator -> itemRegistrator.modelFromBlockParent(blockModelLocation));
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry location as block has.
         * This method doesn't generate item model automatically, so if you want to generate it, do it by yourselves in {@code itemSettings} consumer.
         *
         * @param group        creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public BlockRegisterChain<B> defaultBlockItem(@Nullable CreativeModeTab group, Consumer<ItemRegisterChain<BlockItem>> itemSettings) {
            return defaultBlockItem(new Item.Properties().tab(group), itemSettings);
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry location as block has.
         * This method doesn't generate item model automatically, so if you want to generate it, do it by yourselves in {@code itemSettings} consumer.
         *
         * @param props        properties, that will be inserted in the item. Can also be created with {@link ItemPropsFactory}
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public BlockRegisterChain<B> defaultBlockItem(Item.Properties props, Consumer<ItemRegisterChain<BlockItem>> itemSettings) {
            return item(() -> new BlockItem(asRegistryObject().get(), props), itemSettings);
        }

        /**
         * Registers the item for this block.
         * It will be with the same registry location as block has.
         *
         * @param itemSupplier item factory, should return new item instance every time it's called.
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public <I extends Item> BlockRegisterChain<B> item(Supplier<I> itemSupplier, Consumer<ItemRegisterChain<I>> itemSettings) {
            ItemRegisterChain<I> itemRegisterChain = BlockRegister.this.itemRegister.register(getName(), itemSupplier);
            itemSettings.accept(itemRegisterChain);
            return this;
        }

        /**
         * Generates and loads blockstate json on the fly.
         * Blockstate will be with only one default variant, which links to provided {@code blockModelLocation}
         *
         * @param blockModelLocation location of block model, which will be called from blockstate json.
         */
        public BlockRegisterChain<B> oneVariantState(BlockModelLocation blockModelLocation) {
            clientSideOnly(() -> state(BlockStateResource.fromBuilder(BlockStateResource.Builder.create().addDefaultVariant(blockModelLocation))));

            return this;
        }

        /**
         * Generates and loads blockstate json on the fly.
         *
         * @param stateResourceSupplier factory, which should return new BlockStateResource instance every time it's called.
         */
        public BlockRegisterChain<B> state(Supplier<BlockStateResource> stateResourceSupplier) {
            clientSideOnly(() -> state(stateResourceSupplier.get()));
            return this;
        }

        /**
         * Generates and loads blockstate json on the fly.
         *
         * @param stateResource blockstate file for this block.
         */
        public BlockRegisterChain<B> state(BlockStateResource stateResource) {
            clientSideOnly(() -> resourceHolder.get().addBlockStateResource(getRegistryName(), stateResource));

            return this;
        }

        /**
         * Generates and loads block model on the fly.
         * Model can be located later with path models/block/<block_registry_name>.json
         *
         * @param blockModelSupplier factory, which should return new BlockModel instance every time it's called.
         */
        public BlockRegisterChain<B> modelWithRegNamePath(Supplier<BlockModel> blockModelSupplier) {
            clientSideOnly(() -> modelWithRegNamePath(blockModelSupplier.get()));

            return this;
        }

        /**
         * Generates and loads block model on the fly.
         * Model can be located later with path models/block/<block_registry_name>.json
         *
         * @param blockModel model for block.
         */
        public BlockRegisterChain<B> modelWithRegNamePath(BlockModel blockModel) {
            clientSideOnly(() -> model(new BlockModelLocation(getModId(), getName()), blockModel));

            return this;
        }

        /**
         * Generates and loads block model on the fly.
         *
         * @param blockModelLocation location, where model will be located.
         * @param blockModelSupplier factory, which should return new BlockModel instance every time it's called.
         */
        public BlockRegisterChain<B> model(BlockModelLocation blockModelLocation, Supplier<BlockModel> blockModelSupplier) {
            clientSideOnly(() -> model(blockModelLocation, blockModelSupplier.get()));

            return this;
        }

        /**
         * Generates and loads block model on the fly.
         *
         * @param blockModelLocation location, where model will be located.
         * @param blockModel         model for this block.
         */
        public BlockRegisterChain<B> model(BlockModelLocation blockModelLocation, BlockModel blockModel) {
            clientSideOnly(() -> resourceHolder.get().addBlockModel(blockModelLocation, blockModel));

            return this;
        }

        /**
         * Generates and loads the default blockstate json (with one model variant) and model (cube-all).
         * Requires texture with this path &lt;modid&gt;/textures/block/&lt;registry_key&gt;.png to be present
         */
        public BlockRegisterChain<B> oneVarStateAndCubeAllModel() {
            clientSideOnly(() -> oneVarStateAndCubeAllModel(new TextureLocation(getModId(), "block/" + getName())));

            return this;
        }

        /**
         * Generates and loads the default blockstate json (with one model variant) and model (cube-all).
         *
         * @param textureLocation texture location, which block cube-all model will use for every side.
         */
        public BlockRegisterChain<B> oneVarStateAndCubeAllModel(TextureLocation textureLocation) {
            if (EnvironmentUtils.isOnPhysicalClient()) {
                modelWithRegNamePath(BlockModels.cubeAllModel(textureLocation));
                oneVariantState(new BlockModelLocation(getModId(), getName()));
            }
            return this;
        }

        /**
         * Adds block entry to {@link LangGeneratorFacade}, which will place all entries in en_us.json file upon {@link GatherDataEvent}.
         * Generator will generate entries only in {@code runData} launch mode.
         *
         * @param enName english name of block
         */
        public BlockRegisterChain<B> name(String enName) {
            runAfterRegistering(() -> BlockRegister.this.getLangGeneratorFacade().addBlockEntry(asRegistryObject().get(), enName));
            return this;
        }

        /**
         * Runs task for current registrator directly after registering object.
         * Entry for {@link #asRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public BlockRegisterChain<B> doAfterRegistering(Consumer<BlockRegisterChain<B>> task) {
            runAfterRegistering(() -> task.accept(this));
            return this;
        }

        /**
         * Runs task for current registrator  on client setup.
         * Entry for {@link #asRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public BlockRegisterChain<B> doOnClientSetup(Consumer<BlockRegisterChain<B>> task) {
            runOnClientSetup(() -> task.accept(this));
            return this;
        }

        /**
         * Utility function, where you can
         * initialize some variables, for example block model location
         * and then use them in genState and genModel for example (without block model location instance re-creation)
         *
         * @param multipleSettings functions to apply on current registrator.
         */
        public BlockRegisterChain<B> also(Consumer<BlockRegisterChain<B>> multipleSettings) {
            multipleSettings.accept(this);
            return this;
        }
    }

    /**
     * Hacky workaround, which allows to reference to render type from both sides without crashing with {@link NoClassDefFoundError}
     */
    public static class RenderTypeWrapper {
        private final RenderType rt;

        public RenderTypeWrapper(RenderType rt) {
            this.rt = rt;
        }

        private RenderType get() {
            return rt;
        }
    }
}
