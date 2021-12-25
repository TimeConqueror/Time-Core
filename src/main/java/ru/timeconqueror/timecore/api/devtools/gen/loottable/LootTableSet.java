package ru.timeconqueror.timecore.api.devtools.gen.loottable;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class LootTableSet {
    private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

    public abstract LootContextParamSet getParameterSet();

    public void forEach(BiConsumer<ResourceLocation, LootTable.Builder> action) {
        lootTables.forEach(action);
    }

    public abstract void register();

    protected void registerLootTable(ResourceLocation resourceLocation, LootTable.Builder table) {
        this.lootTables.put(resourceLocation, table);
    }
}
