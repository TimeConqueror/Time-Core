package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Config is the main, the highest section, and can contain both sections and properties.
 */
public abstract class Config extends ConfigSection {
    /**
     * <i>Please, see description in ModConfig.Type</i>
     *
     * @see ModConfig.Type
     */
    @NotNull
    private final ModConfig.Type type;

    /**
     * @param type    <i>please, see description in ModConfig.Type</i>
     * @param key     used as a location for the config file (see {@link #getRelativePath()}). Also determines the section in config file and is used as a part of lang keys.
     * @param comment used to provide a comment that can be seen above this section in the config file.
     */
    public Config(@NotNull ModConfig.Type type, @NotNull String key, @Nullable String comment) {
        super(key, comment);
        this.type = type;
    }

    /**
     * Returns the relative path to the file, where config will be saved and read.
     * <br>
     * Example:
     * {@code lootgames/minesweeper.toml} will be saved in {@code config/lootgames/minesweeper.toml}.
     */
    @NotNull
    public String getRelativePath() {
        return getKey() + ".toml";
    }

    @NotNull
    public ModConfig.Type getType() {
        return type;
    }
}
