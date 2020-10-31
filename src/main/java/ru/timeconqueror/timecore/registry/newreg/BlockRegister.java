package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
import ru.timeconqueror.timecore.registry.newreg.ItemRegister.ItemRegisterChain;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockRegister extends ForgeRegister<Block> {
    private final ItemRegister itemRegister;

    public BlockRegister(String modid) {
        super(ForgeRegistries.BLOCKS, modid);
        itemRegister = new ItemRegister(modid);
    }

    public <B extends Block> BlockRegisterChain<B> register(String name, Supplier<B> entrySup) {
        RegistryObject<B> holder = registerEntry(name, entrySup);

        return new BlockRegisterChain<>(holder);
    }

    @Override
    public void regToBus(IEventBus bus) {
        super.regToBus(bus);
        itemRegister.regToBus(bus);
    }

    public class BlockRegisterChain<B extends Block> extends ForgeRegister.RegisterChain<B> {
        public BlockRegisterChain(RegistryObject<B> holder) {
            super(BlockRegister.this, holder);
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry location as block has.
         * It will also generate default item model automatically based on the block one.
         *
         * @param group creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         */
        public BlockRegisterChain<B> regDefaultBlockItem(@Nullable ItemGroup group) {
            return regDefaultBlockItem(group, itemRegistrator -> itemRegistrator.genModelFromBlockParent(new BlockModelLocation(getModId(), getName())));
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry location as block has.
         * It will also generate default item model automatically based on the block one.
         *
         * @param group              creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         * @param blockModelLocation parent block model location for auto-generated item model based on block one.
         */
        public BlockRegisterChain<B> regDefaultBlockItem(@Nullable ItemGroup group, BlockModelLocation blockModelLocation) {
            return regDefaultBlockItem(group, itemRegistrator -> itemRegistrator.genModelFromBlockParent(blockModelLocation));
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry location as block has.
         * This method doesn't generate item model automatically, so if you want to generate it, do it by yourselves in {@code itemSettings} consumer.
         *
         * @param group        creative tab in which item will be placed. Can be null, which means that item will be placed nowhere.
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public BlockRegisterChain<B> regDefaultBlockItem(@Nullable ItemGroup group, Consumer<ItemRegisterChain<BlockItem>> itemSettings) {
            return regDefaultBlockItem(new Item.Properties().tab(group), itemSettings);
        }

        /**
         * Registers the default item block for this block. (which will place the block upon clicking)
         * It will be with the same registry location as block has.
         * This method doesn't generate item model automatically, so if you want to generate it, do it by yourselves in {@code itemSettings} consumer.
         *
         * @param props        properties, that will be inserted in the item. Can also be created with {@link ItemPropsFactory}
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public BlockRegisterChain<B> regDefaultBlockItem(Item.Properties props, Consumer<ItemRegisterChain<BlockItem>> itemSettings) {
            return regItem(() -> new BlockItem(asRegistryObject().get(), props), itemSettings);
        }

        /**
         * Registers the item for this block.
         * It will be with the same registry location as block has.
         *
         * @param itemSupplier item factory, should return new item instance every time it's called.
         * @param itemSettings extra stuff, that you can do for that item, like generating item model.
         */
        public <I extends Item> BlockRegisterChain<B> regItem(Supplier<I> itemSupplier, Consumer<ItemRegisterChain<I>> itemSettings) {
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
        public BlockRegisterChain<B> genDefaultState(BlockModelLocation blockModelLocation) {
            return genState(() -> new BlockStateResource().addDefaultVariant(blockModelLocation));
        }

        /**
         * Generates and loads blockstate json on the fly.
         *
         * @param stateResourceSupplier factory, which should return new BlockStateResource instance every time it's called.
         */
        public BlockRegisterChain<B> genState(Supplier<BlockStateResource> stateResourceSupplier) {
            runTaskOnClientSetup(() -> TimeClient.RESOURCE_HOLDER.addBlockStateResource(asRegistryObject().get(), stateResourceSupplier.get()));

            return this;
        }

        /**
         * Generates and loads block model on the fly.
         * Model can be located later with path models/block/<block_registry_name>.json
         *
         * @param blockModelSupplier factory, which should return new BlockModel instance every time it's called.
         */
        public BlockRegisterChain<B> genModelWithRegistryKeyPath(Supplier<BlockModel> blockModelSupplier) {
            return genModel(new BlockModelLocation(getModId(), getName()), blockModelSupplier);
        }

        /**
         * Generates and loads block model on the fly.
         *
         * @param blockModelLocation location, where model will be located.
         * @param blockModelSupplier factory, which should return new BlockModel instance every time it's called.
         */
        public BlockRegisterChain<B> genModel(BlockModelLocation blockModelLocation, Supplier<BlockModel> blockModelSupplier) {
            runOnlyForClient(() -> TimeClient.RESOURCE_HOLDER.addBlockModel(blockModelLocation, blockModelSupplier.get()));
            return this;
        }

        /**
         * Generates and loads the default blockstate json (with one model variant) and model (cube-all).
         * Requires texture with this path &lt;modid&gt;/textures/block/&lt;registry_key&gt;.png to be present
         */
        public BlockRegisterChain<B> genDefaultStateAndModel() {
            genModelWithRegistryKeyPath(() -> BlockModel.createCubeAllModel(new TextureLocation(getModId(), "block/" + getName())));
            genDefaultState(new BlockModelLocation(getModId(), getName()));
            return this;
        }

        /**
         * Generates and loads the default blockstate json (with one model variant) and model (cube-all).
         *
         * @param textureLocation texture location, which block cube-all model will use for every side.
         */
        public BlockRegisterChain<B> genDefaultStateAndModel(TextureLocation textureLocation) {
            genModelWithRegistryKeyPath(() -> BlockModel.createCubeAllModel(textureLocation));
            genDefaultState(new BlockModelLocation(getModId(), getName()));
            return this;
        }

        /**
         * Adds block entry to {@link LangGeneratorFacade}, which will place all entries in en_us.json file upon {@link GatherDataEvent}.
         * Generator will generate entries only in {@code runData} launch mode.
         *
         * @param enName english localization location of block
         */
        public BlockRegisterChain<B> genLangEntry(String enName) {
            runTaskAfterRegistering(() -> LangGeneratorFacade.addBlockEntry(asRegistryObject().get(), enName));
            return this;
        }

        /**
         * Runs task for current registrator directly after registering object.
         * Entry for {@link #asRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public BlockRegisterChain<B> addActionAfterRegistering(Consumer<BlockRegisterChain<B>> task) {
            runTaskAfterRegistering(() -> task.accept(this));
            return this;
        }

        /**
         * Runs task for current registrator  on client setup.
         * Entry for {@link #asRegistryObject()} is already registered in this moment, so it can be retrieved inside this task.
         */
        public BlockRegisterChain<B> addActionOnClientSetup(Consumer<BlockRegisterChain<B>> task) {
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
        public BlockRegisterChain<B> apply(Consumer<BlockRegisterChain<B>> multipleSettings) {
            multipleSettings.accept(this);
            return this;
        }
    }
}
