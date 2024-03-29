//package ru.timeconqueror.timecore.api.devtools.gen.loottable;
//
//import com.google.common.collect.Maps;
//import com.google.common.collect.Multimap;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.data.DataProvider;
//import net.minecraft.data.HashCache;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.level.storage.loot.LootTable;
//import net.minecraft.world.level.storage.loot.LootTables;
//import net.minecraft.world.level.storage.loot.ValidationContext;
//import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
//import org.jetbrains.annotations.NotNull;
//import ru.timeconqueror.timecore.TimeCore;
// //TODO port
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class TimeLootTableGenerator implements DataProvider {
//    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
//    private final DataGenerator dataGenerator;
//    private final List<LootTableSet> setList = new ArrayList<>();
//
//    public TimeLootTableGenerator(DataGenerator dataGenerator) {
//        this.dataGenerator = dataGenerator;
//    }
//
//    private static Path getPath(Path pathIn, ResourceLocation id) {
//        return pathIn.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
//    }
//
//    @Override
//    public void run(@NotNull HashCache cache) {
//        Path outputFolder = this.dataGenerator.getOutputFolder();
//        Map<ResourceLocation, LootTable> map = Maps.newHashMap();
//
//        setList.forEach(lootTableSet -> {
//            lootTableSet.register();
//
//            lootTableSet.forEach((resourceLocation, builder) -> {
//                if (map.put(resourceLocation, builder.setParamSet(lootTableSet.getParameterSet()).build()) != null) {
//                    throw new IllegalStateException("Duplicate loot table: " + resourceLocation);
//                }
//            });
//        });
//
//        ValidationContext validationtracker = new ValidationContext(LootContextParamSets.ALL_PARAMS, (p_229442_0_) -> null, map::get);
//
//        validate(map, validationtracker);
//
//        Multimap<String, String> multimap = validationtracker.getProblems();
//        if (!multimap.isEmpty()) {
//            multimap.forEach((p_218435_0_, p_218435_1_) -> {
//                TimeCore.LOGGER.warn("Found validation problem in " + p_218435_0_ + ": " + p_218435_1_);
//            });
//            throw new IllegalStateException("Failed to validate loot tables, see logs");
//        } else {
//            map.forEach((resourceLocation, lootTable) -> {
//                Path path = getPath(outputFolder, resourceLocation);
//
//                try {
//                    DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
//                } catch (IOException ioexception) {
//                    TimeCore.LOGGER.error("Couldn't save loot table {}", path, ioexception);
//                }
//
//            });
//        }
//    }
//
//    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
//        map.forEach((resourceLocation, lootTable) -> LootTables.validate(validationtracker, resourceLocation, lootTable));
//    }
//
//    public TimeLootTableGenerator addSet(LootTableSet set) {
//        setList.add(set);
//        return this;
//    }
//
//    @NotNull
//    @Override
//    public String getName() {
//        return "TimeCore LootTable Generator";
//    }
//}
