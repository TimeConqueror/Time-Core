package ru.timeconqueror.timecore.devtools.gen.loottable;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootTable;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class LootTableSet {
    private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

    public abstract LootParameterSet getParameterSet();

    public void forEach(BiConsumer<ResourceLocation, LootTable.Builder> action) {
        lootTables.forEach(action);
    }

    public abstract void register();

    protected void registerLootTable(ResourceLocation resourceLocation, LootTable.Builder table) {
        this.lootTables.put(resourceLocation, table);
    }
}
