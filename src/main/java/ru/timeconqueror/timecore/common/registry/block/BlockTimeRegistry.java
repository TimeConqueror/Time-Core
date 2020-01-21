package ru.timeconqueror.timecore.common.registry.block;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.timeconqueror.timecore.api.ITimeMod;
import ru.timeconqueror.timecore.api.client.TimeClient;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource;
import ru.timeconqueror.timecore.api.client.resource.ModelBlock;
import ru.timeconqueror.timecore.api.client.resource.location.ModelBlockLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.common.registry.ForgeTimeRegistry;

import java.util.Objects;

public abstract class BlockTimeRegistry extends ForgeTimeRegistry<Block> {

    public BlockTimeRegistry(ITimeMod mod) {
        super(mod);
    }

    @SubscribeEvent
    public final void onRegBlocksEvent(RegistryEvent.Register<Block> event) {
        onFireRegistryEvent(event);
    }

    /**
     * Register block with given name.
     *
     * @param name will be used in registry and localization key.
     */
    public BlockWrapper regBlock(Block block, String name) {
        name = name.toLowerCase();
        return new BlockWrapper(block, name);
    }

    public class BlockWrapper extends EntryWrapper {
        public BlockWrapper(Block block, String name) {
            super(block, name);
        }

        public BlockWrapper regModel(ModelBlock model) {
            TimeClient.RESOURCE_HOLDER.addBlockModel(getEntry(), model);
            return this;
        }

        public BlockWrapper regModel(ModelBlockLocation path, ModelBlock model) {
            TimeClient.RESOURCE_HOLDER.addBlockModel(path, model);
            return this;
        }

        public BlockWrapper regDefaultBlockState(ModelBlockLocation blockModel) {
            regBlockState(new BlockStateResource().addDefaultVariant(blockModel));
            return this;
        }

        public BlockWrapper regDefaultBlockStateAndModel(TextureLocation blockTexture) {
            regModel(ModelBlock.createCubeAllModel(blockTexture));

            ModelBlockLocation modelLocation = new ModelBlockLocation(getMod().getModID(), Objects.requireNonNull(getEntry().getRegistryName()).getPath());
            regDefaultBlockState(modelLocation);

            return this;
        }

        public BlockWrapper regBlockState(BlockStateResource blockState) {
            TimeClient.RESOURCE_HOLDER.addBlockStateResource(getEntry(), blockState);

            return this;
        }
    }
}
