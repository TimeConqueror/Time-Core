package ru.timeconqueror.timecore.storage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Collection;
import java.util.EnumSet;

public class StructureTags {
    private static final Multimap<Tag, Structure<?>> TAGS = HashMultimap.create();

    /**
     * Only for calling during loading stages.
     */
    public static synchronized void put(Tag tag, Structure<?> structure) {
        TAGS.put(tag, structure);
    }

    public static synchronized void put(EnumSet<Tag> tags, Structure<?> structure) {
        for (Tag tag : tags) {
            TAGS.put(tag, structure);
        }
    }

    /**
     * Only for calling after loading is complete.
     */
    public static Collection<Structure<?>> get(Tag tag) {
        return TAGS.get(tag);
    }

    public enum Tag {
        /**
         * If applied, the structure won't be broken by lakes.
         */
        DISABLE_BREAKING_BY_LAKES
    }
}
