package ru.timeconqueror.timecore.api.devtools.gen.lang;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultSections {
    public static final Supplier<LangSection<CreativeModeTab>> ITEM_GROUPS = () -> new LangSection<>("Creative Tabs", itemGroup -> itemGroup.getDisplayName().getString());
    public static final Supplier<LangSection<Block>> BLOCKS = () -> new LangSection<>("Blocks", Block::getDescriptionId);
    public static final Supplier<LangSection<Item>> ITEMS = () -> new LangSection<>("Items", Item::getDescriptionId);
    public static final Supplier<LangSection<ArmorItem>> ARMOR = () -> new LangSection<ArmorItem>("Armor", Item::getDescriptionId)
            .setSortingComparator(Comparator.<Map.Entry<ArmorItem, String>, String>comparing(mapEntry -> mapEntry.getKey().getMaterial().getName())
                    .thenComparingInt((mapEntry) -> {
                        EquipmentSlot armorType = mapEntry.getKey().getEquipmentSlot();
                        if (armorType == EquipmentSlot.HEAD) return 1;
                        if (armorType == EquipmentSlot.CHEST) return 2;
                        if (armorType == EquipmentSlot.LEGS) return 3;
                        if (armorType == EquipmentSlot.FEET) return 4;

                        return 5;
                    })
            );
    public static final Supplier<LangSection<EntityType<?>>> ENTITIES = () -> new LangSection<>("Entities", EntityType::getDescriptionId);
    public static final Supplier<LangSection<String>> MISC = () -> new LangSection<>("Miscellaneous", s -> s);
}
