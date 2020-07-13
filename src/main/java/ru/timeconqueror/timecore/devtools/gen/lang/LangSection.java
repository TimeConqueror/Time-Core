package ru.timeconqueror.timecore.devtools.gen.lang;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LangSection<T> {
    public static final LangSection<ItemGroup> ITEM_GROUPS = new LangSection<>("Creative Tabs", ItemGroup::getTranslationKey);
    public static final LangSection<Block> BLOCKS = new LangSection<>("Blocks", Block::getTranslationKey);
    public static final LangSection<Item> ITEMS = new LangSection<>("Items", Item::getTranslationKey);
    public static final LangSection<ArmorItem> ARMOR = new LangSection<ArmorItem>("Armor", Item::getTranslationKey)
            .setSortingComparator(Comparator.<Map.Entry<ArmorItem, String>, String>comparing(mapEntry -> mapEntry.getKey().getArmorMaterial().getName())
                    .thenComparingInt((mapEntry) -> {
                        EquipmentSlotType armorType = mapEntry.getKey().getEquipmentSlot();
                        if (armorType == EquipmentSlotType.HEAD) return 1;
                        if (armorType == EquipmentSlotType.CHEST) return 2;
                        if (armorType == EquipmentSlotType.LEGS) return 3;
                        if (armorType == EquipmentSlotType.FEET) return 4;

                        return 5;
                    })
            );
    public static final LangSection<EntityType<?>> ENTITIES = new LangSection<>("Entities", EntityType::getTranslationKey);
    public static final LangSection<String> MISC = new LangSection<>("Miscellaneous", s -> s);

    private final HashMap<T, String> entries = new HashMap<>();
    private final String name;
    private final Function<T, String> keyCreator;
    private Comparator<Map.Entry<T, String>> sorter = null;
    private boolean saved = false;

    public LangSection(String name, @NotNull Function<T, String> keyCreator) {
        this.name = name;
        this.keyCreator = keyCreator;
    }

    public LangSection<T> setSortingComparator(@Nullable Comparator<Map.Entry<T, String>> sorter) {
        this.sorter = sorter;

        return this;
    }

    public void addEntry(T entry, String enName) {
        if (saved)
            throw new IllegalStateException("Entry map has already been saved. You should add entries only before there were dumped to file.");
        entries.put(entry, enName);
    }

    public String getComment() {
        return "#" + name;
    }

    public String getName() {
        return name;
    }

    public String createKey(T entry) {
        return keyCreator.apply(entry);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    void sendEntries(Map<String, String> out) {
        entries.entrySet()
                .stream()
                .sorted(sorter != null ? sorter : Comparator.<Map.Entry<T, String>, String>comparing(o -> createKey(o.getKey()).toLowerCase()))
                .forEach(entry -> out.put(createKey(entry.getKey()), entry.getValue()));

        entries.clear();

        saved = true;
    }
}
