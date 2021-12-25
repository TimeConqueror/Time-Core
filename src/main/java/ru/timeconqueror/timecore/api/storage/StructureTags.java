package ru.timeconqueror.timecore.api.storage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

import java.util.Collection;
import java.util.EnumSet;

public class StructureTags {
    private static final Multimap<Tag, StructureFeature<?>> TAGS = HashMultimap.create();

    /**
     * Only for calling during loading stages.
     */
    public static synchronized void put(Tag tag, StructureFeature<?> structure) {
        TAGS.put(tag, structure);
    }

    public static synchronized void put(EnumSet<Tag> tags, StructureFeature<?> structure) {
        for (Tag tag : tags) {
            TAGS.put(tag, structure);
        }
    }

    /**
     * Only for calling after loading is complete.
     */
    public static Collection<StructureFeature<?>> get(Tag tag) {
        return TAGS.get(tag);
    }

    public enum Tag {
        /**
         * If applied, the structure won't be broken by lakes.
         */
        DISABLE_BREAKING_BY_LAKES
    }
}
