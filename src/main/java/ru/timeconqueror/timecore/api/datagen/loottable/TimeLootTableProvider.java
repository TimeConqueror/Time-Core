package ru.timeconqueror.timecore.api.datagen.loottable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.ValidationResults;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeLootTableProvider implements IDataProvider {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator dataGenerator;
    private final List<LootTableSet> setList = new ArrayList<>();

    public TimeLootTableProvider(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    private static Path getPath(Path pathIn, ResourceLocation id) {
        return pathIn.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
    }

    @Override
    public void act(@NotNull DirectoryCache cache) {
        Path outputFolder = this.dataGenerator.getOutputFolder();
        Map<ResourceLocation, LootTable> map = Maps.newHashMap();

        setList.forEach(lootTableSet -> {
            lootTableSet.register();

            lootTableSet.forEach((resourceLocation, builder) -> {
                if (map.put(resourceLocation, builder.setParameterSet(lootTableSet.getParameterSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table: " + resourceLocation);
                }
            });
        });

        ValidationResults validationresults = new ValidationResults();

        validate(map, validationresults);

        Multimap<String, String> multimap = validationresults.getProblems();
        if (!multimap.isEmpty()) {
            multimap.forEach((p_218435_0_, p_218435_1_) -> {
                TimeCore.LOGGER.warn("Found validation problem in " + p_218435_0_ + ": " + p_218435_1_);
            });
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        } else {
            map.forEach((resourceLocation, lootTable) -> {
                Path path = getPath(outputFolder, resourceLocation);

                try {
                    IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
                } catch (IOException ioexception) {
                    TimeCore.LOGGER.error("Couldn't save loot table {}", path, ioexception);
                }

            });
        }
    }

    protected void validate(Map<ResourceLocation, LootTable> map, ValidationResults validationresults) {
        map.forEach((resourceLocation, lootTable) -> LootTableManager.func_215302_a(validationresults, resourceLocation, lootTable, map::get));
    }

    public void addLootTableSet(LootTableSet set) {
        setList.add(set);
    }

    @NotNull
    @Override
    public String getName() {
        return "Time LootTables";
    }
}
