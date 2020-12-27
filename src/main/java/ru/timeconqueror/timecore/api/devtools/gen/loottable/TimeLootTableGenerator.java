package ru.timeconqueror.timecore.api.devtools.gen.loottable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeLootTableGenerator implements IDataProvider {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator dataGenerator;
    private final List<LootTableSet> setList = new ArrayList<>();

    public TimeLootTableGenerator(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    private static Path getPath(Path pathIn, ResourceLocation id) {
        return pathIn.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
    }

    @Override
    public void run(@NotNull DirectoryCache cache) {
        Path outputFolder = this.dataGenerator.getOutputFolder();
        Map<ResourceLocation, LootTable> map = Maps.newHashMap();

        setList.forEach(lootTableSet -> {
            lootTableSet.register();

            lootTableSet.forEach((resourceLocation, builder) -> {
                if (map.put(resourceLocation, builder.setParamSet(lootTableSet.getParameterSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table: " + resourceLocation);
                }
            });
        });

        ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.ALL_PARAMS, (p_229442_0_) -> null, map::get);

        validate(map, validationtracker);

        Multimap<String, String> multimap = validationtracker.getProblems();
        if (!multimap.isEmpty()) {
            multimap.forEach((p_218435_0_, p_218435_1_) -> {
                TimeCore.LOGGER.warn("Found validation problem in " + p_218435_0_ + ": " + p_218435_1_);
            });
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        } else {
            map.forEach((resourceLocation, lootTable) -> {
                Path path = getPath(outputFolder, resourceLocation);

                try {
                    IDataProvider.save(GSON, cache, LootTableManager.serialize(lootTable), path);
                } catch (IOException ioexception) {
                    TimeCore.LOGGER.error("Couldn't save loot table {}", path, ioexception);
                }

            });
        }
    }

    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((resourceLocation, lootTable) -> LootTableManager.validate(validationtracker, resourceLocation, lootTable));
    }

    public TimeLootTableGenerator addSet(LootTableSet set) {
        setList.add(set);
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return "Time LootTables";
    }
}
