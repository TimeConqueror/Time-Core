package ru.timeconqueror.timecore.util;

/**
 * Used for more readable key creation.
 * No more visible string concatenation, which spoils the view!
 * <blockquote><pre><s>new TranslationTextComponent("cmd." + "TimeCore" + ".subscribe.list")</s></pre></blockquote>
 * <blockquote><pre>new TranslationTextComponent(YourMod.langResolver.commandKey("subscribe.list"))</pre></blockquote>
 */
public class I18nResolver {
    private final String modId;

    public I18nResolver(String modId) {
        this.modId = modId;
    }

    public String commandKey(String postfix) {
        return "cmd." + modId + "." + postfix;
    }
}
