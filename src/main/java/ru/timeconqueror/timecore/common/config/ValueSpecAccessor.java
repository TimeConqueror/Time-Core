package ru.timeconqueror.timecore.common.config;

import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.reflection.UnlockedField;

import java.util.List;

public class ValueSpecAccessor {
    private static final UnlockedField<String> fValueSpecComment = ReflectionHelper.findField(ValueSpec.class, "comment");
    private static final UnlockedField<String> fValueSpecLangKey = ReflectionHelper.findField(ValueSpec.class, "langKey");

    public static void addLinesToComment(ValueSpec spec, List<String> additions) {
        String comment = fValueSpecComment.get(spec);

        StringBuilder b = new StringBuilder(comment != null ? comment : "");

        for (String addition : additions) {
            b.append("\n").append(addition);
        }

        fValueSpecComment.set(spec, b.toString());
    }

    public static void setLangKey(ValueSpec spec, String key) {
        fValueSpecLangKey.set(spec, key);
    }
}
