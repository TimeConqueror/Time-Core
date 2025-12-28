package ru.timeconqueror.timecore.api.devtools.gen.loottable;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@Log4j2
@Getter
public abstract class TimeBlockLootSubProvider extends BlockLootSubProvider {
    private final String modId;

    @Setter
    private boolean errorUponMissingLootTable = true;

    public TimeBlockLootSubProvider(String modId, Set<Item> explosionImmuneItems) {
        super(explosionImmuneItems, FeatureFlags.REGISTRY.allFlags());
        this.modId = modId;
    }

    protected TimeBlockLootSubProvider(String modId, Set<Item> explosionImmuneItems, FeatureFlagSet requiredFeatures) {
        super(explosionImmuneItems, requiredFeatures);
        this.modId = modId;
    }

    protected TimeBlockLootSubProvider(String modId, Set<Item> explosionImmuneItems, FeatureFlagSet requiredFeatures, Map<ResourceLocation, LootTable.Builder> lootTables) {
        super(explosionImmuneItems, requiredFeatures, lootTables);
        this.modId = modId;
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer_) {
        // almost copy-pasted from BlockLootSubProvider#generate(BiConsumer)

        this.generate();
        Set<ResourceLocation> set = new HashSet<>();

        for (Block block : getKnownBlocks()) {
            ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
            if (!key.getNamespace().equals(modId)) {
                continue;
            }

            if (block.isEnabled(this.enabledFeatures)) {
                ResourceLocation resourcelocation = block.getLootTable();
                if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                    LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                    if (loottable$builder == null) {
                        String message = String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourcelocation, key);
                        if (errorUponMissingLootTable) {
                            throw new IllegalStateException(message);
                        } else {
                            log.warn(message);
                            continue;
                        }
                    }

                    biConsumer_.accept(resourcelocation, loottable$builder);
                }
            }
        }

        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }
}
