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
    private final String modId;
    private final ModelProvider modelProvider;

    public BlockStateProvider(PackOutput output, String modId) {
        this.output = output;
        this.modId = modId;
        this.modelProvider = new ModelProvider(output, modId) {
            @Override
            protected void registerAll() {

            }

            @Override
            public String getName() {
                return BlockStateProvider.this.getName() + ": " + "Internal Model Provider";
            }
        };
    }

    public ModelProvider modelProvider() {
        return modelProvider;
    }

    protected void regBlockState(Block block, BlockStateResource resource) {
        if (registeredStates.containsKey(block)) {
            log.warn("BlockState resource for block {} already exists, skipping...", block);
            return;
        }

        registeredStates.putIfAbsent(block, resource);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        registeredStates.clear();
        modelProvider.clear();
        registerAll();

        CompletableFuture<?>[] futures = new CompletableFuture<?>[1 + this.registeredStates.size()];
        int i = 0;
        futures[i++] = modelProvider.generateAll(cache);
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