package ru.timeconqueror.timecore.registry.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.WrappedForgeTimeRegistry;
import ru.timeconqueror.timecore.registry.item.ItemPropsFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Used for simplifying block adding. You need to extend it and do your stuff in {@link #register()} method<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link TimeAutoRegistrable} with INSTANCE Target
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 * Examples can be seen at test module.
 */
public abstract class BlockTimeRegistry extends WrappedForgeTimeRegistry<Block> {
    /**
     * Should be used only before calling {@link #onRegItemsEvent(RegistryEvent.Register)}.
     * After calling that method it won't do anything and will become null.
     */
    private ArrayList<BlockItem> regItems = new ArrayList<>();

    @SubscribeEvent
    public final void onRegBlocksEvent(RegistryEvent.Register<Block> event) {
        onFireRegistryEvent(event);
    }

    @SubscribeEvent
    public final void onRegItemsEvent(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (BlockItem regItem : regItems) {
            registry.register(regItem);
        }

        regItems.clear();
        regItems = null;
    }

    /**
     * Method to register provided block automatically.
     * You also don't need to think about registry side for some methods, it will do this for you.
     *
     * @param block block to register
     * @param name  block name. Will be used as a part of registry and translation keys. Should NOT contain mod ID, because it will be bound automatically.
     * @return {@link BlockWrapper} to provide extra register options, like blockstate, model and itemblock registering.
     */
    public BlockWrapper regBlock(Block block, String name) {
        return new BlockWrapper(block, name);
    }

    /**
     * Wrapper class to register extra options, like models, blockstates and item blocks without need of json-files!
     */
    public class BlockWrapper extends EntryWrapper {

        public BlockWrapper(Block block, String name) {
            super(block, name);
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry name as block has.
         * It will also generate default item model automatically based on the block one.
         *
         * @param group creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         */
        public BlockWrapper regDefaultBlockItem(@Nullable ItemGroup group) {
            return regDefaultBlockItem(group, new BlockModelLocation(getModID(), getId().getPath()));
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry name as block has.
         * It will also generate default item model automatically based on the block one.
         *
         * @param group              creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         * @param blockModelLocation parent block model location for auto-generated item model based on block one.
         */
        public BlockWrapper regDefaultBlockItem(@Nullable ItemGroup group, BlockModelLocation blockModelLocation) {
            return regDefaultBlockItem(group, () -> new ItemModel(blockModelLocation));
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry name as block has.
         * This method doesn't generate item model automatically, so if you want to generate it, do it by yourselves in {@code itemSettings} consumer.
         *
         * @param group             creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         * @param itemModelSupplier supplier that returns model to be used for itemblock.
         *                          Supplier is used here to call its content only for client side, so all stuff that is returned by it,
         *                          likely should not be created outside lambda (except locations).
         *                          For details see {@link ItemModel}
         */
        public BlockWrapper regDefaultBlockItem(@Nullable ItemGroup group, Supplier<ItemModel> itemModelSupplier) {
            return regDefaultBlockItem(new Item.Properties().group(group), itemModelSupplier);
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry name as block has.
         * This method doesn't generate item model automatically, so if you want to generate it, do it by yourselves in {@code itemSettings} consumer.
         *
         * @param props             properties, that will be inserted in the item. Can also be created with {@link ItemPropsFactory}
         * @param itemModelSupplier supplier that returns model to be used for itemblock.
         *                          Supplier is used here to call its content only for client side, so all stuff that is returned by it,
         *                          likely should not be created outside lambda (except locations).
         *                          For details see {@link ItemModel}
         */
        public BlockWrapper regDefaultBlockItem(Item.Properties props, Supplier<ItemModel> itemModelSupplier) {
            return regItem(() -> new BlockItem(getBlock(), props), itemModelSupplier);
        }

        /**
         * Registers the item for this block.
         * It will be with the same registry name as block has.
         *
         * @param itemSupplier      item factory, should return new item instance every time it's called.
         * @param itemModelSupplier supplier that returns model to be used for itemblock.
         *                          Supplier is used here to call its content only for client side, so all stuff that is returned by it,
         *                          likely should not be created outside lambda (except locations).
         *                          For details see {@link ItemModel}
         */
        public BlockWrapper regItem(Supplier<? extends BlockItem> itemSupplier, Supplier<ItemModel> itemModelSupplier) {
            BlockItem item = itemSupplier.get();
            item.setRegistryName(getId());

            regItems.add(item);

            runForClient(() -> TimeClient.RESOURCE_HOLDER.addItemModel(item, itemModelSupplier.get()));
            return this;
        }

        /**
         * Generates and loads blockstate json on the fly.
         * Blockstate will be with only one default variant, which links to provided {@code blockModelLocation}
         *
         * @param blockModelLocation location of block model, which will be called from blockstate json.
         */
        public BlockWrapper genDefaultState(BlockModelLocation blockModelLocation) {
            return genState(() -> new BlockStateResource().addDefaultVariant(blockModelLocation));
        }

        /**
         * Generates and loads blockstate json on the fly.
         *
         * @param stateResourceSupplier factory, which should return new BlockStateResource instance every time it's called.
         */
        public BlockWrapper genState(Supplier<BlockStateResource> stateResourceSupplier) {
            runForClient(() -> TimeClient.RESOURCE_HOLDER.addBlockStateResource(getBlock(), stateResourceSupplier.get()));

            return this;
        }

        /**
         * Generates and loads block model on the fly.
         * Model can be located later with path models/block/<block_registry_name>.json
         *
         * @param blockModelSupplier factory, which should return new BlockModel instance every time it's called.
         */
        public BlockWrapper genModelWithRegistryKeyPath(Supplier<BlockModel> blockModelSupplier) {
            return genModel(new BlockModelLocation(getId().getNamespace(), getId().getPath()), blockModelSupplier);
        }

        /**
         * Generates and loads block model on the fly.
         *
         * @param blockModelLocation location, where model will be located.
         * @param blockModelSupplier factory, which should return new BlockModel instance every time it's called.
         */
        public BlockWrapper genModel(BlockModelLocation blockModelLocation, Supplier<BlockModel> blockModelSupplier) {
            runForClient(() -> TimeClient.RESOURCE_HOLDER.addBlockModel(blockModelLocation, blockModelSupplier.get()));
            return this;
        }

        /**
         * Generates and loads the default blockstate json (with one model variant) and model (cube-all).
         * Requires texture with this path &lt;modid&gt;/textures/block/&lt;registry_key&gt;.png to be present
         */
        public BlockWrapper genDefaultStateAndModel() {
            genModelWithRegistryKeyPath(() -> BlockModel.createCubeAllModel(new TextureLocation(getId().getNamespace(), "block/" + getId().getPath())));
            genDefaultState(new BlockModelLocation(getId().getNamespace(), getId().getPath()));
            return this;
        }

        /**
         * Generates and loads the default blockstate json (with one model variant) and model (cube-all).
         *
         * @param textureLocation texture location, which block cube-all model will use for every side.
         */
        public BlockWrapper genDefaultStateAndModel(TextureLocation textureLocation) {
            genModelWithRegistryKeyPath(() -> BlockModel.createCubeAllModel(textureLocation));
            genDefaultState(new BlockModelLocation(getId().getNamespace(), getId().getPath()));
            return this;
        }


        /**
         * Register default item representation of current block.
         * Automatically creates item model parent where default block model is a parent.
         * <p>
         * Item model will be available later with path "modid:models/item/block_name".
         *
         * @param group Item Group in which generated itemblock will be added (used in Creative GUI).
         * @return {@link BlockWrapper} to provide extra register options, like block state and models registering.
         */
        public BlockWrapper regItemBlock(ItemGroup group) {
            Item.Properties props = new ItemPropsFactory(group).createProps();

            return regItemBlock(props);
        }

        /**
         * Register item representation of current block with given properties.
         * Automatically creates item model parent where default block model (model path: "modid:block/block_name", full: "modid:models/block/block_name") is a parent.
         * <p>
         * Item model will be available later with path "modid:models/item/block_name".
         *
         * @param props properties that will be applied to the item block. Can be created via {@link ItemPropsFactory}
         * @return {@link BlockWrapper} to provide extra register options, like block state and models registering.
         * @see ItemPropsFactory
         */
        public BlockWrapper regItemBlock(Item.Properties props) {
            ResourceLocation registryName = Objects.requireNonNull(getBlock().getRegistryName());

            return regItemBlock(props, new BlockModelLocation(getModID(), registryName.getPath()));
        }

        /**
         * Register item representation of current block with given properties and blockmodel location as a parent for item model.
         * <p>
         * Item model will be available later with path "modid:models/item/block_name".
         *
         * @param props              properties that will be applied to the item block. Can be created via {@link ItemPropsFactory}
         * @param blockModelLocation location that will be used during item model creation as its parent. For details see {@link BlockModelLocation}
         * @return {@link BlockWrapper} to provide extra register options, like block state and models registering.
         * @see ItemPropsFactory
         */
        public BlockWrapper regItemBlock(Item.Properties props, BlockModelLocation blockModelLocation) {
            return regItemBlock(props, () -> new ItemModel(blockModelLocation));
        }

        /**
         * Register item representation of current block with given properties and item model.
         * Automatically registers given model.
         * <p>
         * Item model will be available later with path "modid:models/item/block_name".
         *
         * @param props             properties that will be applied to the item block. Can be created via {@link ItemPropsFactory}
         * @param itemModelSupplier supplier that returns model to be used for itemblock.
         *                          Supplier is used here to call its content only for client side, so all stuff that is returned by it,
         *                          likely should not be created outside lambda (except locations).
         *                          For details see {@link ItemModel}
         * @return {@link BlockWrapper} to provide extra register options, like block state and models registering.
         * @see ItemPropsFactory
         */
        public BlockWrapper regItemBlock(Item.Properties props, Supplier<ItemModel> itemModelSupplier) {
            BlockItem item = new BlockItem(getBlock(), props);

            ResourceLocation registryName = Objects.requireNonNull(getBlock().getRegistryName());
            item.setRegistryName(registryName);

            regItems.add(item);

            runForClient(() -> TimeClient.RESOURCE_HOLDER.addItemModel(item, itemModelSupplier.get()));

            return this;
        }

        /**
         * Returns block bound to wrapper.
         * Method duplicates {@link #getEntry()}, so it exists only for easier understanding.
         */
        public Block getBlock() {
            return getEntry();
        }
    }
}
