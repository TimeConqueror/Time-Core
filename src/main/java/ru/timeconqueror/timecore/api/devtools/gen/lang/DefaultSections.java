package ru.timeconqueror.timecore.api.devtools.gen.lang;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultSections {
    public static final Supplier<LangSection<ItemGroup>> ITEM_GROUPS = () -> new LangSection<>("Creative Tabs", itemGroup -> itemGroup.getDisplayName().getString());
    public static final Supplier<LangSection<Block>> BLOCKS = () -> new LangSection<>("Blocks", Block::getDescriptionId);
    public static final Supplier<LangSection<Item>> ITEMS = () -> new LangSection<>("Items", Item::getDescriptionId);
    public static final Supplier<LangSection<ArmorItem>> ARMOR = () -> new LangSection<ArmorItem>("Armor", Item::getDescriptionId)
            .setSortingComparator(Comparator.<Map.Entry<ArmorItem, String>, String>comparing(mapEntry -> mapEntry.getKey().getMaterial().getName())
                    .thenComparingInt((mapEntry) -> {
                        EquipmentSlotType armorType = mapEntry.getKey().getSlot();
                        if (armorType == EquipmentSlotType.HEAD) return 1;
                        if (armorType == EquipmentSlotType.CHEST) return 2;
                        if (armorType == EquipmentSlotType.LEGS) return 3;
                        if (armorType == EquipmentSlotType.FEET) return 4;

                        return 5;
                    })
            );
    public static final Supplier<LangSection<EntityType<?>>> ENTITIES = () -> new LangSection<>("Entities", EntityType::getDescriptionId);
    public static final Supplier<LangSection<String>> MISC = () -> new LangSection<>("Miscellaneous", s -> s);
}
