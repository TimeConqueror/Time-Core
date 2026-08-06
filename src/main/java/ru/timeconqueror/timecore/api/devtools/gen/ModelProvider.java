package ru.timeconqueror.timecore.api.devtools.gen;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.JSONTimeResource;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.ItemModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ModelProvider implements DataProvider {
    private final Logger log = LogManager.getLogger();

    protected static final ResourceType TEXTURE = new ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");
    protected static final ResourceType MODEL = new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models");
    protected static final ResourceType MODEL_WITH_EXTENSION = new ResourceType(PackType.CLIENT_RESOURCES, "", "models");

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final PackOutput output;
    protected final String modid;
    @VisibleForTesting
    public final Map<ResourceLocation, JSONTimeResource> generatedModels = new HashMap<>();

    public ModelProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    protected abstract void registerAll();

    public ResourceLocation regModel(ModelType type, String path, JSONTimeResource resource) {
        Preconditions.checkNotNull(path, "Path must not be null");
        ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path), type);
        if (generatedModels.containsKey(outputLoc)) {
            throw new IllegalArgumentException("Model with path %s already exists".formatted(outputLoc));
        }

        generatedModels.put(outputLoc, resource);
        return outputLoc;
    }

    public BlockModelLocation regBlockModel(String path, BlockModel resource) {
        ResourceLocation location = regModel(ModelType.BLOCK, path, resource);
        return new BlockModelLocation(location.getNamespace(), location.getPath());
    }

    public BlockModelLocation regBlockModel(Block block, BlockModel resource) {
        return regBlockModel(getId(block).toString(), resource);
    }

    public ItemModelLocation regItemModel(String path, ItemModel resource) {
        ResourceLocation location = regModel(ModelType.ITEM, path, resource);
        return new ItemModelLocation(location.getNamespace(), location.getPath());
    }

    public ItemModelLocation regItemModel(Item item, ItemModel resource) {
        return regItemModel(getId(item).toString(), resource);
    }

    public ItemModelLocation regItemModelParentedByBlock(Block block) {
        Item item = block.asItem();
        ResourceLocation id = getId(block);
        return regItemModel(item, ItemModel.parentedBy(new BlockModelLocation(id)));
    }

    public ItemModelLocation regItemModelParentedByBlock(Block block, BlockModelLocation location) {
        return regItemModel(block.asItem(), ItemModel.parentedBy(location));
    }

    private ResourceLocation extendWithFolder(ResourceLocation rl, ModelType type) {
        if (rl.getPath().contains("/")) {
            return rl;
        }
        return new ResourceLocation(rl.getNamespace(), type.folderName() + "/" + rl.getPath());
    }

    public TextureLocation getDefaultTextureLocation(Block block) {
        ResourceLocation id = getId(block);
        return new TextureLocation(id.getNamespace(), ModelType.BLOCK.folderName() + "/" + id.getPath());
    }

    public BlockModelLocation getDefaultModelLocation(Block block) {
        ResourceLocation id = getId(block);
        return new BlockModelLocation(id.getNamespace(), id.getPath());
    }

    public ResourceLocation getId(Block block) {
        //noinspection deprecation
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public ResourceLocation getId(Item item) {
        //noinspection deprecation
        return BuiltInRegistries.ITEM.getKey(item);
    }

    protected void clear() {
        generatedModels.clear();
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        clear();
        registerAll();
        return generateAll(cache);
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache) {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[this.generatedModels.size()];
        int i = 0;

        for (Map.Entry<ResourceLocation, JSONTimeResource> e : generatedModels.entrySet()) {
            Path target = getPath(e.getKey());
            String json = e.getValue().toJson();

            futures[i++] = DataProvider.saveStable(cache, GSON.fromJson(json, JsonElement.class), target);
        }

        return CompletableFuture.allOf(futures);
    }

    protected Path getPath(ResourceLocation loc) {
        return this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(loc.getNamespace())
                .resolve("models")
                .resolve(loc.getPath() + ".json");
    }
}
