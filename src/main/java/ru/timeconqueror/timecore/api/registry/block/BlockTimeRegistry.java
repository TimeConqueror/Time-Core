package ru.timeconqueror.timecore.api.registry.block;

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
import ru.timeconqueror.timecore.api.registry.WrappedForgeTimeRegistry;
import ru.timeconqueror.timecore.api.registry.item.ItemPropsFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Used for simplifying block adding.<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link ru.timeconqueror.timecore.api.registry.TimeAutoRegistry}
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
         * Registers blockstate with single default variant,
         * block model with standard block texture path for all sides (it also binds this model to single blockstate variant),
         * and itemblock (item, that represents block).
         *
         * @param group if not null, then generated itemblock will be added to this Item Group (Creative Tabs).
         *              if null, itemblock won't be found from Creative GUI.
         */
        public void regDefaults(@Nullable ItemGroup group) {
            regDefaults(new TextureLocation(getModID(), "block/" + getBlock().getRegistryName().getPath()), group);
        }

        /**
         * Registers blockstate with single default variant,
         * block model with one given texture for all sides (it also binds this model to single blockstate variant),
         * and itemblock (item, that represents block).
         *
         * @param blockTexture texture that will be used for all sides of the block. For details see {@link TextureLocation}.
         * @param group        if not null, then generated itemblock will be added to this Item Group (Creative Tabs).
         *                     if null, itemblock won't be found from Creative GUI.
         */
        public void regDefaults(TextureLocation blockTexture, @Nullable ItemGroup group) {
            regDefaultStateAndModel(blockTexture);

            ItemPropsFactory propsFactory = group != null ? new ItemPropsFactory(group) : new ItemPropsFactory(properties -> {
            });
            regItemBlock(propsFactory.createProps());
        }

        /**
         * Registers blockstate with single default variant,
         * block model from given one (it also binds this model to single blockstate variant),
         * and itemblock (item, that represents block).
         *
         * @param blockModelSupplier supplier that returns model to be used in blockstate. For details see {@link BlockModel}.
         * @param group              if not null, then generated itemblock will be added to this Item Group (Creative Tabs).
         *                           if null, itemblock won't be found from Creative GUI.
         */
        public void regDefaults(Supplier<BlockModel> blockModelSupplier, @Nullable ItemGroup group) {
            regDefaultStateAndModel(blockModelSupplier);

            ItemPropsFactory propsFactory = group != null ? new ItemPropsFactory(group) : new ItemPropsFactory(properties -> {
            });
            regItemBlock(propsFactory.createProps());
        }

        /**
         * Registers blockstate with single default variant
         * and given model (with standard block model location ("modid:block/block_name", full path: "modid:models/block/block_name")),
         * also binds model to the single blockstate variant.
         *
         * @param blockModelSupplier supplier for block model you want to register.
         *                           Supplier is used here to call its content only for client side, so all stuff that is returned by it
         *                           likely should not be created outside lambda (except locations).
         *                           For details see {@link BlockModel}.
         * @return {@link BlockWrapper} to provide extra register options, like itemblock registering.
         */
        public BlockWrapper regDefaultStateAndModel(Supplier<BlockModel> blockModelSupplier) {
            ResourceLocation location = Objects.requireNonNull(getBlock().getRegistryName());

            regDefaultModel(blockModelSupplier);

            regDefaultState(new BlockModelLocation(location.getNamespace(), location.getPath()));

            return this;
        }

        /**
         * Registers blockstate with single default variant
         * and block model with one given {@code blockTexture} for all sides (it also binds this model to single blockstate variant).
         * <p>
         * Block model will be available later with path "modid:models/block/block_name", or "modid:block/block_name" if used in blockstates.
         *
         * @param blockTexture texture that will be used for all sides of the block. For details see {@link TextureLocation}.
         * @return {@link BlockWrapper} to provide extra register options, like itemblock registering.
         */
        public BlockWrapper regDefaultStateAndModel(TextureLocation blockTexture) {
            regDefaultModel(() -> BlockModel.createCubeAllModel(blockTexture));

            ResourceLocation registryName = Objects.requireNonNull(getBlock().getRegistryName());
            BlockModelLocation modelLocation = new BlockModelLocation(registryName.getNamespace(), registryName.getPath());
            regDefaultState(modelLocation);

            return this;
        }

        /**
         * Registers blockstate with single default variant
         * and binds model from given {@code blockModelLocation} to single blockstate variant.
         *
         * @param blockModelLocation location, where you want to register block model. For details see {@link BlockModelLocation}.
         * @return {@link BlockWrapper} to provide extra register options, like block model and itemblock registering.
         */
        public BlockWrapper regDefaultState(BlockModelLocation blockModelLocation) {
            return regState(() -> new BlockStateResource().addDefaultVariant(blockModelLocation));
        }

        /**
         * Registers blockstate with given blockstate resource.
         * Blockstate will be available later with path "modid:blockstates/block_name".
         *
         * @param stateResourceSupplier supplier for blockstate resource (the alternative to json file you made in assets/modid/blockstates/...).
         *                              Supplier is used here to call its content only for client side, so all stuff that is returned by it
         *                              likely should not be created outside lambda (except locations).
         *                              For details see {@link BlockStateResource}.
         * @return {@link BlockWrapper} to provide extra register options, like block model and itemblock registering.
         */
        public BlockWrapper regState(Supplier<BlockStateResource> stateResourceSupplier) {
            TimeClient.RESOURCE_HOLDER.addBlockStateResource(getBlock(), stateResourceSupplier.get());

            return this;
        }

        /**
         * Registers model with standard block model location ("modid:block/block_name", full path: "modid:models/block/block_name")
         *
         * @param blockModelSupplier supplier for block model you want to register.
         *                           Supplier is used here to call its content only for client side, so all stuff that is returned by it
         *                           likely should not be created outside lambda (except locations).
         *                           For details about model see {@link BlockModel}.
         * @return {@link BlockWrapper} to provide extra register options, like blockstate, another model and itemblock registering.
         */
        public BlockWrapper regDefaultModel(Supplier<BlockModel> blockModelSupplier) {
            ResourceLocation registryName = Objects.requireNonNull(getBlock().getRegistryName());
            return regModel(new BlockModelLocation(registryName.getNamespace(), registryName.getPath()), blockModelSupplier);
        }

        /**
         * Registers model with given location.
         *
         * @param blockModelLocation location, where you want to register block model. For details see {@link BlockModelLocation}.
         * @param blockModelSupplier supplier for block model you want to register.
         *                           Supplier is used here to call its content only for client side, so all stuff that is returned by it,
         *                           likely should not be created outside lambda (except locations).
         *                           For details about model see {@link BlockModel}.
         * @return {@link BlockWrapper} to provide extra register options, like blockstate, another model and itemblock registering.
         */
        public BlockWrapper regModel(BlockModelLocation blockModelLocation, Supplier<BlockModel> blockModelSupplier) {
            runForClient(() -> TimeClient.RESOURCE_HOLDER.addBlockModel(blockModelLocation, blockModelSupplier.get()));
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
