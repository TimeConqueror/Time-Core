package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.mixins.config.IConfigValueEditable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Improved builder which automatically adds lang keys, comments while building config properties.
 * Also it has a support of auto extendable lang prefixes.
 */
public class ImprovedConfigBuilder extends ForgeConfigSpec.Builder {

    private String modid;
    private ConfigSectionHolder section;

    /**
     * If true the builder will add line with default value to comments.
     */
    private boolean defValueToComment;

    /**
     * If true the builder will add prefixes, that are pushed in during deepening in subsections, to properties' lang keys.
     */
    private boolean autoLangKey;

    private Stack<String> i18nPrefix = new Stack<>();
    private List<String> commentAdditions = new ArrayList<>();

    public ImprovedConfigBuilder(ConfigSectionHolder section) {
        this(section, true, true);
    }

    /**
     * @param addDefaultValueToComment if true system will add line with default value to comments.
     * @param autoGenLangKey           if true the builder will add prefixes, that are pushed in during deepening in subsections, to properties' lang keys.
     */
    public ImprovedConfigBuilder(ConfigSectionHolder section, boolean addDefaultValueToComment, boolean autoGenLangKey) {
        modid = ModLoadingContext.get().getActiveNamespace();
        this.section = section;
        this.defValueToComment = addDefaultValueToComment;
        this.autoLangKey = autoGenLangKey;

        comment(section.getComment());
        pushWithLang(section.getKey());
    }

    /**
     * Just adds extra functionality to the base define method.
     * It isn't necessary to use it, you'd better look at {@link ForgeConfigSpec.Builder} define methods.
     */
    @Override
    public <T> ForgeConfigSpec.ConfigValue<T> define(List<String> path, ForgeConfigSpec.ValueSpec value, Supplier<T> defaultSupplier) {
        IConfigValueEditable valueEditable = (IConfigValueEditable) value;


        if (defValueToComment) commentAdditions.add("Default: " + value.getDefault());

        commentAdditions.forEach(valueEditable::addLineToComment);
        commentAdditions.clear();

        if (autoLangKey)
            valueEditable.setLangKey("cfg." + modid + "." + StringUtils.join(i18nPrefix, '.') + StringUtils.join(path, '.'));

        return super.define(path, value, defaultSupplier);
    }

    /**
     * Setups provided subsection and adds it to bound holder ({@link #section}).
     */
    public void addAndSetupSection(ConfigSection section) {
        addAndSetupSection(section, null, null);
    }

    /**
     * Setups provided subsection and adds it to bound holder ({@link #section}).
     *
     * @param sectionIn     subsection, which you want to add to holder.
     * @param customComment if not null it will be set above category in config file,
     *                      otherwise {@code sectionIn#getComment()} will be used.
     */
    public void addAndSetupSection(ConfigSection sectionIn, @Nullable String customComment) {
        addAndSetupSection(sectionIn, null, customComment);
    }

    /**
     * Setups provided subsection and adds it to bound holder ({@link #section}).
     *
     * @param sectionIn        subsection, which you want to add to holder.
     * @param customI18nPrefix if not null it will be added to lang key to be set in config property during its creation.
     *                         otherwise {@code sectionIn#getKey()} will be used.
     * @param customComment    if not null it will be set above category in config file,
     *                         otherwise {@code sectionIn#getComment()} will be used.
     */
    public void addAndSetupSection(ConfigSection sectionIn, @Nullable String customI18nPrefix, @Nullable String customComment) {
        String comment;
        if (customComment == null || customComment.isEmpty()) {
            comment = sectionIn.getComment();
        } else comment = customComment;

        if (comment != null && !comment.isEmpty()) comment(comment);

        this.section.addSection(sectionIn);

        ConfigSectionHolder prevSection = this.section;
        this.section = sectionIn;

        push(sectionIn.getKey());

        String langKeyApp;
        if (customI18nPrefix == null || customI18nPrefix.isEmpty()) {
            langKeyApp = sectionIn.getKey();
        } else langKeyApp = customI18nPrefix;

        boolean applyKeyApp = !langKeyApp.isEmpty();
        if (applyKeyApp) pushI18nPrefix(langKeyApp);

        sectionIn.setup(this);

        if (applyKeyApp) popLangKeyPrefix();

        pop();

        this.section = prevSection;
    }

    /**
     * Enters in the section with provided {@code path}.
     * Also adds this path as a prefix to lang key to be set in config property during its creation.
     *
     * @see #addAndSetupSection(ConfigSection)
     */
    public ForgeConfigSpec.Builder pushWithLang(@NotNull String path) {
        pushI18nPrefix(path);
        return super.push(path);
    }

    /**
     * Enters in the section with provided {@code path}.
     *
     * @param i18nPrefix prefix that will be added to lang key to be set in config property during its creation.
     * @see #addAndSetupSection(ConfigSection)
     */
    public ForgeConfigSpec.Builder pushWithLang(String path, @NotNull String i18nPrefix) {
        pushI18nPrefix(i18nPrefix);
        return super.push(path);
    }

    /**
     * Adds provided prefix to lang key that will be set in config property during its creation.
     */
    public void pushI18nPrefix(@NotNull String prefix) {
        if (prefix.isEmpty()) throw new IllegalStateException("Prefix shouldn't be empty.");
        i18nPrefix.push(prefix);
    }

    /**
     * Removes last added prefix from lang key.
     */
    public void popLangKeyPrefix() {
        if (!i18nPrefix.empty()) {
            i18nPrefix.pop();
        }
    }

    @Override
    public ForgeConfigSpec build() {
        return super.build();
    }
}