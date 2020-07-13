package ru.timeconqueror.timecore.devtools.gen.lang;

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
 * Generates lang entries and places them in en_us.lang.
 * It doesn't delete en_us.lang!
 * If you don't have any special marks in your lang file,
 * it will generate stuff in its end.
 * <p>
 * All public methods here are runtime-safely. While it works in dev workspace, it will be quietly turned off in runtime.
 * <p>
 * If you have stuff to be generated not in the end of the file, you should place
 * <b>#MARK AUTO GEN START</b> and <b>#MARK AUTO GEN END</b> on any two lines of file, where you want to see generated stuff.
 * Be careful, all lines that are between these two marks will be deleted before generating, so don't place custom lang entries there.
 * <p>
 * Example:
 * <blockquote>
 * <pre>
 *  entity.journey.sentrywalker.name=Sentry Walker
 *
 *  #MARK AUTO GEN START
 *  <- (here generator will create all its entries) ->
 *  #MARK AUTO GEN END
 *
 *  advancement.journey.hello=Hello!
 *  </pre>
 * </blockquote>
 */
public class LangGeneratorFacade {
    private static final LangJsonGenerator GENERATOR = new LangJsonGenerator();
    private static final HashMap<String, LangSection<?>> SECTIONS = new LinkedHashMap<>();

    static {
        addSection(LangSection.CREATIVE_TABS);
        addSection(LangSection.BLOCKS);
        addSection(LangSection.ITEMS);
        addSection(LangSection.ARMOR);
        addSection(LangSection.ENTITIES);
        addSection(LangSection.MISC);
    }

    public static void addItemEntry(Item item, String enName) {
        if (shouldSave()) {
            addLangEntry(LangSection.ITEMS, item, enName);
        }
    }

    public static void addEntityEntry(EntityType<?> entityEntry, String enName) {
        if (shouldSave()) {
            addLangEntry(LangSection.ENTITIES, entityEntry, enName);
        }
    }

    public static void addItemGroupEntry(ItemGroup itemGroup, String enName) {
        if (shouldSave()) {
            addLangEntry(LangSection.CREATIVE_TABS, itemGroup, enName);
        }
    }

    public static void addArmorEntry(ArmorItem item, String enName) {
        if (shouldSave()) {
            EquipmentSlotType equipmentSlot = item.getEquipmentSlot();

            String suffix;
            switch (equipmentSlot) {
                case HEAD:
                    suffix = "%material% Helmet";
                    break;
                case CHEST:
                    suffix = "%material% Chestplate";
                    break;
                case LEGS:
                    suffix = "%material% Leggings";
                    break;
                case FEET:
                    suffix = "%material% Boots";
                    break;
                default:
                    throw new IllegalStateException("Unsupported equipment slot: " + equipmentSlot);
            }

            addArmorEntry(item, enName, suffix);
        }
    }

    /**
     * @param nameSuffix represents full or base localized name that may contain %material% mark which will be replaced with provided material name.
     */
    public static void addArmorEntry(ArmorItem item, String materialEnName, String nameSuffix) {
        if (shouldSave()) {
            String name = nameSuffix.replace("%material%", materialEnName);
            addLangEntry(LangSection.ARMOR, item, name);
        }
    }

    public static void addMiscEntry(String key, String name) {
        if (!shouldSave()) return;
        addLangEntry(LangSection.MISC, key, name);
    }

    public static <T> LangSection<T> addSection(LangSection<T> langSection) {
        if (SECTIONS.put(langSection.getName(), langSection) != null) {
            throw new RuntimeException("Lang section with name " + langSection.getName() + " already exists.");
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
