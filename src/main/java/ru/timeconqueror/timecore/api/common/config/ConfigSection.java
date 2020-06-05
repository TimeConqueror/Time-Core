package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;

public abstract class ConfigSection {
    private ArrayList<ConfigSection> sections;

    /**
     * Determines the section in config file and also is used as a part of lang keys.
     * <br>
     * <font color=yellow>If it's a {@link Config}, it will be also used as a name for the config file.</font>
     */
    @NotNull
    private final String key;
    /**
     * Used to provide a comment that can be seen above this section in the config file.
     */
    @Nullable
    private final String comment;

    public ConfigSection(@NotNull String key, @Nullable String comment) {
        this.key = key;
        this.comment = comment;
    }

    /**
     * Here you should setup all your config values and sections.<br>
     * Do NOT call {@link ImprovedConfigBuilder#build()} here, it will be called later automatically.<br>
     * <p>
     * Example:
     * <blockquote><pre>
     * public ForgeConfigSpec.IntValue DETONATION_TIME;
     * public ConfigSectionImpl STAGE_1;
     *
     * &#64;Override
     * public void setup() {
     *  DETONATION_TIME = builder.comment("The time until bombs start to explode. Represented in ticks.")
     *  .defineInRange("detonation_time", 3 * 20, 0, 600);
     *  STAGE_1 = new ConfigSectionImpl("y_stage_1");
     *  builder.addAndSetupSection(STAGE_1, "stage", "Regulates characteristics of stage 1.");
     * }
     * </pre></blockquote>
     */
    public abstract void setup(ImprovedConfigBuilder builder);

    /**
     * Adds subsection to this section.
     * <p>
     * Called by {@link ImprovedConfigBuilder} during adding subcategories in {@link ImprovedConfigBuilder#addAndSetupSection}.
     */
    void addSection(ConfigSection category) {
        if (sections == null) sections = new ArrayList<>();

        sections.add(category);
    }

    /**
     * Called on every loading and reloading of bound config file.
     */
    @OverridingMethodsMustInvokeSuper
    public void onEveryLoad(final ModConfig.ModConfigEvent configEvent) {
        if (sections != null) sections.forEach(configSection -> configSection.onEveryLoad(configEvent));
    }

    public @Nullable String getComment() {
        return comment;
    }

    public @NotNull String getKey() {
        return key;
    }

    /**
     * Should return new builder instance to be used in {@code setup} methods.
     */
    protected ImprovedConfigBuilder getBuilder() {
        return new ImprovedConfigBuilder(this);
    }
}
