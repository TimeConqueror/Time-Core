package ru.timeconqueror.timecore.api.common.config;

/**
 * Represents the section of config.
 * Config may contain many of them.
 * <p>
 * ConfigSection can contain both sections and properties.
 */
public abstract class ConfigSection extends ConfigSectionHolder {
    /**
     * Here you should setup all your config values and subsections.
     * You should use provided {@code configBuilder} for it.
     */
    public abstract void setup(ImprovedConfigBuilder configBuilder);
}
