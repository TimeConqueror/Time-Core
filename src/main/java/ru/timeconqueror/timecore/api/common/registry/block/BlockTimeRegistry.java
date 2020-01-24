package ru.timeconqueror.timecore.api.common.registry.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.ITimeMod;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.common.registry.ForgeTimeRegistry;
import ru.timeconqueror.timecore.api.common.registry.item.ItemPropertiesFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Registry that should be extended and annotated with {@link ru.timeconqueror.timecore.api.common.registry.TimeAutoRegistry},
 * if you want to register blocks.
 * <p>
 * Examples can be seen at test module.
 */
public abstract class BlockTimeRegistry extends ForgeTimeRegistry<Block> {
    private ArrayList<BlockItem> regItems = new ArrayList<>();

    public BlockTimeRegistry(ITimeMod mod) {
        super(mod);
    }

    @SubscribeEvent
    public final void onRegBlocksEvent(RegistryEvent.Register<Block> event) {
        onFireRegistryEvent(event);
    }

    @SubscribeEvent
    public final void onRegItemsEvent(RegistryEvent.Register<Item> event) {
        forceBoundModLoading(() -> {
            for (BlockItem itemBlock : regItems) {
                event.getRegistry().register(itemBlock);
            }
        });
    }

    /**
     * Method to register provided block automatically.
     *
     * @param block block to register
     * @param name  block name. Will be used as a part of registry and translation keys. Should NOT contain mod ID, because it will be bound automatically.
     * @return {@link BlockWrapper} to provide extra register options, like blockstate, model and itemblock registering.
     */
    public BlockWrapper regBlock(Block block, String name) {
        name = name.toLowerCase();
        return new BlockWrapper(block, name);
    }

    /**
     * Wrapper class to register extra options, like models, blockstates and item blocks without need of json-files!
     */
    public class BlockWrapper extends EntryWrapper {
        private ClientWrapper clientWrapper;

        public BlockWrapper(Block block, String name) {
            super(block, name);

            clientWrapper = new ClientWrapper(getBlock());
        }

        public BlockWrapper regOnClient(Consumer<ClientWrapper> clientStuff) {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                clientStuff.accept(clientWrapper);
            }

            return this;
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
            regOnClient(clientWrapper -> clientWrapper.regDefaultStateAndModel(blockTexture));

            ItemPropertiesFactory propsFactory = group != null ? new ItemPropertiesFactory(group) : new ItemPropertiesFactory(properties -> {
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
            regOnClient(clientWrapper -> clientWrapper.regDefaultStateAndModel(blockModelSupplier.get()));

            ItemPropertiesFactory propsFactory = group != null ? new ItemPropertiesFactory(group) : new ItemPropertiesFactory(properties -> {
            });
            regItemBlock(propsFactory.createProps());
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
            Item.Properties props = new ItemPropertiesFactory(group).createProps();

            return regItemBlock(props);
        }

        /**
         * Register item representation of current block with given properties.
         * Automatically creates item model parent where default block model (model path: "modid:block/block_name", full: "modid:models/block/block_name") is a parent.
         * <p>
         * Item model will be available later with path "modid:models/item/block_name".
         *
         * @param props properties that will be applied to the item block. Can be created via {@link ItemPropertiesFactory}
         * @return {@link BlockWrapper} to provide extra register options, like block state and models registering.
         * @see ItemPropertiesFactory
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
         * @param props              properties that will be applied to the item block. Can be created via {@link ItemPropertiesFactory}
         * @param blockModelLocation location that will be used during item model creation as its parent. For details see {@link BlockModelLocation}
         * @return {@link BlockWrapper} to provide extra register options, like block state and models registering.
         * @see ItemPropertiesFactory
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
         * @param props             properties that will be applied to the item block. Can be created via {@link ItemPropertiesFactory}
         * @param itemModelSupplier supplier that returns model to be used for itemblock. For details see {@link ItemModel}
         * @return {@link BlockWrapper} to provide extra register options, like block state and models registering.
         * @see ItemPropertiesFactory
         */
        public BlockWrapper regItemBlock(Item.Properties props, Supplier<ItemModel> itemModelSupplier) {
            BlockItem item = new BlockItem(getBlock(), props);

            ResourceLocation registryName = Objects.requireNonNull(getBlock().getRegistryName());
            item.setRegistryName(registryName);

            regItems.add(item);

            regOnClient(clientWrapper -> TimeClient.RESOURCE_HOLDER.addItemModel(item, itemModelSupplier.get()));

            return this;
        }

        public Block getBlock() {
            return getEntry();
        }
    }

    public class ClientWrapper {
        private Block block;

        private ClientWrapper(Block block) {
            this.block = block;
        }

        /**
         * Registers model with standard block model location ("modid:block/block_name", full path: "modid:models/block/block_name")
         *
         * @param blockModel block model you want to register. For details see {@link BlockModel}.
         * @return {@link BlockWrapper} to provide extra register options, like blockstate, another model and itemblock registering.
         */
        public ClientWrapper regDefaultModel(BlockModel blockModel) {
            TimeClient.RESOURCE_HOLDER.addBlockModel(getBlock(), blockModel);
            return this;
        }

        /**
         * Registers model with given location.
         *
         * @param blockModelLocation location, where you want to register block model. For details see {@link BlockModelLocation}.
         * @param blockModel         block model you want to register. For details see {@link BlockModel}.
         * @return {@link BlockWrapper} to provide extra register options, like blockstate, another model and itemblock registering.
         */
        public ClientWrapper regModel(BlockModelLocation blockModelLocation, BlockModel blockModel) {
            TimeClient.RESOURCE_HOLDER.addBlockModel(blockModelLocation, blockModel);
            return this;
        }

        /**
         * Registers blockstate with single default variant
         * and given model (with standard block model location ("modid:block/block_name", full path: "modid:models/block/block_name")),
         * also binds model to the single blockstate variant.
         *
         * @param blockModel block model you want to register. For details see {@link BlockModel}.
         * @return {@link BlockWrapper} to provide extra register options, like itemblock registering.
         */
        public ClientWrapper regDefaultStateAndModel(BlockModel blockModel) {
            ResourceLocation location = Objects.requireNonNull(getBlock().getRegistryName());

            regDefaultModel(blockModel);

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
        public ClientWrapper regDefaultStateAndModel(TextureLocation blockTexture) {
            regDefaultModel(BlockModel.createCubeAllModel(blockTexture));

            ResourceLocation registryName = Objects.requireNonNull(getBlock().getRegistryName());
            BlockModelLocation modelLocation = new BlockModelLocation(getMod().getModID(), registryName.getPath());
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
        public ClientWrapper regDefaultState(BlockModelLocation blockModelLocation) {
            return regState(new BlockStateResource().addDefaultVariant(blockModelLocation));
        }

        /**
         * Registers blockstate with given blockstate resource.
         * Blockstate will be available later with path "modid:blockstates/block_name".
         *
         * @param stateResource blockstate resource (the alternative to json file you made in assets/modid/blockstates/...). For details see {@link BlockStateResource}.
         * @return {@link BlockWrapper} to provide extra register options, like block model and itemblock registering.
         */
        public ClientWrapper regState(BlockStateResource stateResource) {
            TimeClient.RESOURCE_HOLDER.addBlockStateResource(getBlock(), stateResource);

            return this;
        }

        public Block getBlock() {
            return block;
        }
    }
}
