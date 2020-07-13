package ru.timeconqueror.timecore.devtools.gen.lang;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import ru.timeconqueror.timecore.util.EnvironmentUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Generates lang entries and places them in en_us.json file.
 * It doesn't delete en_us.json!
 * If you don't have any special marks in your lang file,
 * it will generate stuff in its end.
 * <p>
 * All public methods here are runtime-safely. While <b>it works in runData launch mode</b>, it is quietly turned off in runClient/runServer or in common runtime.
 * <p>
 * If you have stuff to be generated not in the end of the file, you should place
 * <b>#MARK AUTO GEN START</b> and <b>#MARK AUTO GEN END</b> on any two lines of file, where you want to see generated stuff.
 * Be careful, all lines that are between these two marks will be deleted before generating, so don't place custom lang entries there.
 * <p>
 * Example:
 * <blockquote>
 * <pre>
 *  entity.timecore.test_entity.name=Test Entity
 *
 *  #MARK AUTO GEN START
 *  <- (here generator will create all its entries) ->
 *  #MARK AUTO GEN END
 *
 *  advancement.timecore.hello=Hello!
 *  </pre>
 * </blockquote>
 */
public class LangGeneratorFacade {
    private static final LangJsonGenerator GENERATOR = new LangJsonGenerator();
    private static final HashMap<String, LangSection<?>> SECTIONS = new LinkedHashMap<>();

    static {
        addSection(LangSection.ITEM_GROUPS);
        addSection(LangSection.BLOCKS);
        addSection(LangSection.ITEMS);
        addSection(LangSection.ARMOR);
        addSection(LangSection.ENTITIES);
        addSection(LangSection.MISC);
    }

    /**
     * Adds item entry to {@link LangSection#ITEMS}, which will be processed by generator on {@link GatherDataEvent}.
     * Generator will generate entries only in {@code runData} launch mode.
     *
     * @param item   item for which english name will be added to file
     * @param enName english localization name of item
     */
    public static void addItemEntry(Item item, String enName) {
        if (shouldSave()) {
            addLangEntry(LangSection.ITEMS, item, enName);
        }
    }

    /**
     * Adds block entry to {@link LangSection#BLOCKS}, which will be processed by generator on {@link GatherDataEvent}.
     * Generator will generate entries only in {@code runData} launch mode.
     *
     * @param block  block for which english name will be added to file
     * @param enName english localization name of block
     */
    public static void addBlockEntry(Block block, String enName) {
        if (shouldSave()) {
            addLangEntry(LangSection.BLOCKS, block, enName);
        }
    }

    /**
     * Adds entity entry to {@link LangSection#ENTITIES}, which will be processed by generator on {@link GatherDataEvent}.
     * Generator will generate entries only in {@code runData} launch mode.
     *
     * @param entityEntry entry of entity for which english name will be added to file
     * @param enName      english localization name of entity
     */
    public static void addEntityEntry(EntityType<?> entityEntry, String enName) {
        if (shouldSave()) {
            addLangEntry(LangSection.ENTITIES, entityEntry, enName);
        }
    }

    /**
     * Adds item group entry to {@link LangSection#ITEM_GROUPS}, which will be processed by generator on {@link GatherDataEvent}.
     * Generator will generate entries only in {@code runData} launch mode.
     *
     * @param itemGroup item group for which english name will be added to file
     * @param enName    english localization name of item group
     */
    public static void addItemGroupEntry(ItemGroup itemGroup, String enName) {
        if (shouldSave()) {
            addLangEntry(LangSection.ITEM_GROUPS, itemGroup, enName);
        }
    }

    /**
     * Adds armor item entry to {@link LangSection#ARMOR}, which will be processed by generator on {@link GatherDataEvent}.
     * Generator will generate entries only in {@code runData} launch mode.<br>
     * <p>
     * This method is only for common armor stuff names, like the "Diamond Helmet", where equipment slot ("Helmet") is the last word.<br>
     * The last word, which represents the equipment slot, will be framed automatically.<br>
     * <p>
     * For uncommon names see {@link #addArmorEntry(ArmorItem, String)} to set name directly.
     *
     * @param item           armor item for which english name will be added to file
     * @param materialEnName the english name of material, will be the first word in the full name
     */
    public static void addArmorEntryByMaterial(ArmorItem item, String materialEnName) {
        if (shouldSave()) {
            EquipmentSlotType equipmentSlot = item.getEquipmentSlot();

            String fullLocalizedName;
            switch (equipmentSlot) {
                case HEAD:
                    fullLocalizedName = materialEnName + "Helmet";
                    break;
                case CHEST:
                    fullLocalizedName = materialEnName + "Chestplate";
                    break;
                case LEGS:
                    fullLocalizedName = materialEnName + "Leggings";
                    break;
                case FEET:
                    fullLocalizedName = materialEnName + "Boots";
                    break;
                default:
                    throw new IllegalStateException("Unsupported equipment slot: " + equipmentSlot);
            }

            addArmorEntry(item, fullLocalizedName);
        }
    }

    /**
     * Adds armor item entry to {@link LangSection#ARMOR}, which will be processed by generator on {@link GatherDataEvent}.
     * Generator will generate entries only in {@code runData} launch mode.<br>
     * <p>
     * This method is only for uncommon armor stuff names, like the "Helmet of The Dark One", where "Helmet" is the first word, not the last.<br>
     * <p>
     * For common names see {@link #addArmorEntryByMaterial(ArmorItem, String)}
     *
     * @param item   armor item for which english name will be added to file
     * @param enName english localization name of item
     */
    public static void addArmorEntry(ArmorItem item, String enName) {
        if (shouldSave()) {
            addLangEntry(LangSection.ARMOR, item, enName);
        }
    }

    /**
     * Adds miscellaneous entry to {@link LangSection#MISC}, which will be processed by generator on {@link GatherDataEvent}.
     * Generator will generate entries only in {@code runData} launch mode.
     *
     * @param key    full localization key of this thing
     * @param enName english localization name of this thing
     */
    public static void addMiscEntry(String key, String enName) {
        if (!shouldSave()) return;
        addLangEntry(LangSection.MISC, key, enName);
    }

    /**
     * Here you can add custom sections, which will be processed later by the generator.
     * Generator will generate entries only in {@code runData} launch mode.
     * <p>
     * All entries should be added before section is written to the file, otherwise exception will be thrown.
     *
     * @param langSection section to be added to the generator
     */
    public static <T> LangSection<T> addSection(LangSection<T> langSection) {
        if (SECTIONS.put(langSection.getName(), langSection) != null) {
            throw new IllegalArgumentException("Lang section with name " + langSection.getName() + " already exists.");
        }

        return langSection;
    }

    /**
     * Adds lang entry to provided section.
     */
    private static <T> void addLangEntry(LangSection<T> section, T entry, String enName) {
        if (shouldSave()) section.addEntry(entry, enName);
    }

    private static boolean shouldSave() {
        return EnvironmentUtils.isInDataMode();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDataEvent(GatherDataEvent event) {
        GENERATOR.save(SECTIONS);
    }
}
