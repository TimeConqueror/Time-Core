package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Config is the main, the highest section, and can contain both sections and properties.
 */
public abstract class Config extends ConfigSectionHolder {
    /**
     * Here you should setup all your config values and sections and return baked config specification.<br>
     * Example:
     * <blockquote><pre>
     * public ForgeConfigSpec.IntValue DETONATION_TIME;
     * public Stage STAGE_1;
     *
     * &#64;Override
     * public ForgeConfigSpec setup() {
     *  ImprovedConfigBuilder builder = new ImprovedConfigBuilder(this);
     *
     *  DETONATION_TIME = builder.comment("The time until bombs start to explode. Represented in ticks.")
     *  .defineInRange("detonation_time", 3 * 20, 0, 600);
     *  STAGE_1 = new Stage("y_stage_1");
     *  builder.addAndSetupSection(STAGE_1, "stage", "Regulates characteristics of stage 1.");
     *
     * return builder.build();
     * }
     * </pre></blockquote>
     */
    public abstract ForgeConfigSpec setup();

    /**
     * Returns the relative path to the file, that will contain this config.
     * <br>
     * Example:
     * {@code lootgames/minesweeper.toml} will be saved in {@code config/lootgames/minesweeper.toml}.
     */
    @NotNull
    public String getRelativePath() {
        return getKey();
    }

    /**
     * @see ModConfig.Type
     */
    public abstract ModConfig.Type getType();
}
