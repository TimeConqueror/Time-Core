package ru.timeconqueror.timecore.registry.deferred;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.devtools.gen.lang.LangGeneratorFacade;
import ru.timeconqueror.timecore.registry.ItemPropsFactory;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.deferred.DeferredItemRegister.ItemRegistrator;
import ru.timeconqueror.timecore.registry.deferred.base.DeferredFMLImplForgeRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Deferred Register for blocks.
 * <p>
 * To work it needs to be in a static field in registry class and be annotated with {@link TimeAutoRegistrable}.
 * Extra params in this annotation are ignored.
 */
public class DeferredBlockRegister extends DeferredFMLImplForgeRegister<Block> {
    private final DeferredItemRegister deferredItemRegister;

    public DeferredBlockRegister(String modid) {
        super(ForgeRegistries.BLOCKS, modid);
        this.deferredItemRegister = new DeferredItemRegister(modid);
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a Block Registrator to do some special stuff upon registering.
     * All methods in {@link BlockRegistrator} are optional.
     *
     * @param name The new block's name, it will automatically have the modid prefixed.
     * @param sup  A factory for the new block, it should return a new instance every time it is called.
     * @return A BlockRegistrator for adding some extra stuff.
     * @see BlockRegistrator
     */
    public <B extends Block> BlockRegistrator<B> regBlock(String name, Supplier<B> sup) {
        return new BlockRegistrator<>(name, sup);
    }

    @Override
    public void regToBus(IEventBus bus) {
        super.regToBus(bus);
        deferredItemRegister.regToBus(bus);
    }

    public class BlockRegistrator<B extends Block> extends Registrator {
        protected BlockRegistrator(String name, Supplier<B> sup) {
            super(name, sup);
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry name as block has.
         * It will also generate default item model automatically based on the block one.
         *
         * @param group creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         */
        public BlockRegistrator<B> regDefaultBlockItem(@Nullable ItemGroup group) {
            return regDefaultBlockItem(group, itemRegistrator -> itemRegistrator.genModelFromBlockParent(new BlockModelLocation(getModid(), getName())));
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry name as block has.
         * It will also generate default item model automatically based on the block one.
         *
         * @param group              creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         * @param blockModelLocation parent block model location for auto-generated item model based on block one.
         */
        public BlockRegistrator<B> regDefaultBlockItem(@Nullable ItemGroup group, BlockModelLocation blockModelLocation) {
            return regDefaultBlockItem(group, itemRegistrator -> itemRegistrator.genModelFromBlockParent(blockModelLocation));
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry name as block has.
         * This method doesn't generate item model automatically, so if you want to generate it, do it by yourselves in {@code itemSettings} consumer.
         *
         * @param group        creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public BlockRegistrator<B> regDefaultBlockItem(@Nullable ItemGroup group, Consumer<ItemRegistrator> itemSettings) {
            return regDefaultBlockItem(new Item.Properties().group(group), itemSettings);
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry name as block has.
         * This method doesn't generate item model automatically, so if you want to generate it, do it by yourselves in {@code itemSettings} consumer.
         *
         * @param props        properties, that will be inserted in the item. Can also be created with {@link ItemPropsFactory}
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public BlockRegistrator<B> regDefaultBlockItem(Item.Properties props, Consumer<ItemRegistrator> itemSettings) {
            return regItem(() -> new BlockItem(getRegistryObject().get(), props), itemSettings);
        }

        /**
         * Registers the item for this block.
         * It will be with the same registry name as block has.
         *
         * @param itemSupplier item factory, should return new item instance every time it's called.
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public BlockRegistrator<B> regItem(Supplier<? extends Item> itemSupplier, Consumer<ItemRegistrator> itemSettings) {
            ItemRegistrator itemRegistrator = deferredItemRegister.regItem(getName(), itemSupplier);
            itemSettings.accept(itemRegistrator);
            return this;
        }

        /**
         * Generates and loads blockstate json on the fly.
         * Blockstate will be with only one default variant, which links to provided {@code blockModelLocation}
         *
         * @param blockModelLocation location of block model, which will be called from blockstate json.
         */
        public BlockRegistrator<B> genDefaultState(BlockModelLocation blockModelLocation) {
            return genState(() -> new BlockStateResource().addDefaultVariant(blockModelLocation));
        }

        /**
         * Generates and loads blockstate json on the fly.
         *
         * @param stateResourceSupplier factory, which should return new BlockStateResource instance every time it's called.
         */
        public BlockRegistrator<B> genState(Supplier<BlockStateResource> stateResourceSupplier) {
            runTaskOnClientSetup(() -> TimeClient.RESOURCE_HOLDER.addBlockStateResource(getRegistryObject().get(), stateResourceSupplier.get()));

            return this;
        }

        /**
         * Generates and loads block model on the fly.
         * Model can be located later with path models/block/<block_registry_name>.json
         *
         * @param blockModelSupplier factory, which should return new BlockModel instance every time it's called.
         */
        public BlockRegistrator<B> genModelWithRegistryKeyPath(Supplier<BlockModel> blockModelSupplier) {
            ResourceLocation registryKey = getRegistryKey();
            return genModel(new BlockModelLocation(registryKey.getNamespace(), registryKey.getPath()), blockModelSupplier);
        }

        /**
         * Generates and loads block model on the fly.
         *
         * @param blockModelLocation location, where model will be located.
         * @param blockModelSupplier factory, which should return new BlockModel instance every time it's called.
         */
        public BlockRegistrator<B> genModel(BlockModelLocation blockModelLocation, Supplier<BlockModel> blockModelSupplier) {
            runOnlyForClient(() -> TimeClient.RESOURCE_HOLDER.addBlockModel(blockModelLocation, blockModelSupplier.get()));
            return this;
        }

        /**
         * Generates and loads the default blockstate json (with one model variant) and model (cube-all).
         * Requires texture with this path &lt;modid&gt;/textures/block/&lt;registry_key&gt;.png to be present
         */
        public BlockRegistrator<B> genDefaultStateAndModel() {
            genModelWithRegistryKeyPath(() -> BlockModel.createCubeAllModel(new TextureLocation(getRegistryKey().getNamespace(), "block/" + getRegistryKey().getPath())));
            genDefaultState(new BlockModelLocation(getRegistryKey().getNamespace(), getRegistryKey().getPath()));
            return this;
        }

        /**
         * Generates and loads the default blockstate json (with one model variant) and model (cube-all).
         *
         * @param textureLocation texture location, which block cube-all model will use for every side.
         */
        public BlockRegistrator<B> genDefaultStateAndModel(TextureLocation textureLocation) {
            genModelWithRegistryKeyPath(() -> BlockModel.createCubeAllModel(textureLocation));
            genDefaultState(new BlockModelLocation(getRegistryKey().getNamespace(), getRegistryKey().getPath()));
            return this;
        }

        /**
         * Adds block entry to {@link LangGeneratorFacade}, which will place all entries in en_us.json file upon {@link GatherDataEvent}.
         * Generator will generate entries only in {@code runData} launch mode.
         *
         * @param enName english localization name of block
         */
        public BlockRegistrator<B> genLangEntry(String enName) {
            runTaskAfterRegistering(() -> LangGeneratorFacade.addBlockEntry(getRegistryObject().get(), enName));
            return this;
        }

        /**
         * Runs task for current registrator directly after registering object.
         * Entry for {@link #getRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public BlockRegistrator<B> doAfterRegistering(Consumer<BlockRegistrator<B>> task) {
            runTaskAfterRegistering(() -> task.accept(this));
            return this;
        }

        /**
         * Runs task for current registrator  on client setup.
         * Entry for {@link #getRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public BlockRegistrator<B> doOnClientSetup(Consumer<BlockRegistrator<B>> task) {
            runTaskOnClientSetup(() -> task.accept(this));
            return this;
        }

        /**
         * Utility function, where you can
         * initialize some variables, for example block model location
         * and then use them in genState and genModel for example (without block model location instance re-creation)
         *
         * @param multipleSettings functions to apply on current registrator.
         */
        public BlockRegistrator<B> apply(Consumer<BlockRegistrator<B>> multipleSettings) {
            multipleSettings.accept(this);
            return this;
        }

        /**
         * The alternative of {@link #end()} method.
         * Will return the typed registry object of block.
         */
        public RegistryObject<B> endTyped() {
            return (RegistryObject<B>) super.end();
        }
    }
}
