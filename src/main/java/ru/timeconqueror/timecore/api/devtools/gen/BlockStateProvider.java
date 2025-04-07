package ru.timeconqueror.timecore.api.devtools.gen;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.VisibleForTesting;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Log4j2
public abstract class BlockStateProvider implements DataProvider {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    @VisibleForTesting
    protected final Map<Block, BlockStateResource> registeredStates = new LinkedHashMap<>();

    private final PackOutput output;
    @Getter
    private final String modid;
    private final ModelProvider blockModelProvider;

    public BlockStateProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
        this.blockModelProvider = new ModelProvider(output, modid, ModelProvider.BLOCK_FOLDER) {
            @Override
            protected void registerAll() {

            }

            @Override
            public String getName() {
                return BlockStateProvider.this.getName() + ": " + "Internal Block Model Provider";
            }
        };
    }

    public ModelProvider blockModels() {
        return blockModelProvider;
    }

    protected void addBlockState(Block block, BlockStateResource resource) {
        if (registeredStates.containsKey(block)) {
            log.warn("BlockState resource for block {} already exists, skipping...", block);
            return;
        }

        registeredStates.putIfAbsent(block, resource);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        registeredStates.clear();
        blockModelProvider.clear();
        registerAll();

        CompletableFuture<?>[] futures = new CompletableFuture<?>[1 + this.registeredStates.size()];
        int i = 0;
        futures[i++] = blockModelProvider.generateAll(cache);
        for (Map.Entry<Block, BlockStateResource> entry : registeredStates.entrySet()) {
            futures[i++] = saveBlockState(cache, GSON.fromJson(entry.getValue().toJson(), JsonObject.class), entry.getKey());
        }
        return CompletableFuture.allOf(futures);
    }

    protected abstract void registerAll();

    private CompletableFuture<?> saveBlockState(CachedOutput cache, JsonObject stateJson, Block owner) {
        ResourceLocation blockName = Preconditions.checkNotNull(getId(owner));
        Path outputPath = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(blockName.getNamespace()).resolve("blockstates").resolve(blockName.getPath() + ".json");
        return DataProvider.saveStable(cache, stateJson, outputPath);
    }

    public ResourceLocation getId(Block block) {
        //noinspection deprecation
        return BuiltInRegistries.BLOCK.getKey(block);
    }

}