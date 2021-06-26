package ru.timeconqueror.timecore.common.config;

import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.api.reflection.UnlockedField;
import ru.timeconqueror.timecore.mixins.accessor.ValueSpecAccessor;

import java.util.List;

public class ConfigValueModifier {
    private static final ThreadLocal<ConfigValueModifier> MODIFIERS = new ThreadLocal<>();

    private final boolean configValueMixinTooLate;
    private UnlockedField<String> fComment;
    private UnlockedField<String> fLangKey;

    public ConfigValueModifier(boolean configValueMixinTooLate) {
        this.configValueMixinTooLate = configValueMixinTooLate;
    }

    public ConfigValueModifier(boolean configValueMixinTooLate, UnlockedField<String> fComment, UnlockedField<String> fLangKey) {
        this.configValueMixinTooLate = configValueMixinTooLate;
        this.fComment = fComment;
        this.fLangKey = fLangKey;
    }

    public static void addLinesToComment(ValueSpec spec, List<String> addition) {
        ConfigValueModifier modifier = getModifier(spec);
        modifier.addLinesToCommentNonStatic(spec, addition);
    }

    public static void setLangKey(ValueSpec spec, String key) {
        ConfigValueModifier modifier = getModifier(spec);
        modifier.setLangKeyNonStatic(spec, key);
    }

    private void addLinesToCommentNonStatic(ValueSpec spec, List<String> additions) {
        String comment;

        if (!configValueMixinTooLate) {
            comment = spec.getComment();

        } else {
            comment = fComment.get(spec);
        }

        StringBuilder b = new StringBuilder(comment != null ? comment : "");

        for (String addition : additions) {
            b.append("\n").append(addition);
        }

        if (!configValueMixinTooLate) {
            ((ValueSpecAccessor) spec).setComment(b.toString());
        } else {
            fComment.set(spec, b.toString());
        }
    }

    private void setLangKeyNonStatic(ValueSpec spec, String key) {
        if (!configValueMixinTooLate) {
            ((ValueSpecAccessor) spec).setLangKey(key);
        } else {
            fLangKey.set(spec, key);
        }
    }

    private static ConfigValueModifier getModifier(ValueSpec spec) {
        ConfigValueModifier modifier = MODIFIERS.get();
        if (modifier == null) {
            try {
                ValueSpecAccessor casted = (ValueSpecAccessor) spec;
                modifier = new ConfigValueModifier(false);
            } catch (ClassCastException e) {
                UnlockedField<String> fComment = ReflectionHelper.findField(ValueSpec.class, "comment");
                UnlockedField<String> fLangKey = ReflectionHelper.findField(ValueSpec.class, "langKey");

                modifier = new ConfigValueModifier(true, fComment, fLangKey);
            }

            MODIFIERS.set(modifier);
        }

        return modifier;
    }
}
