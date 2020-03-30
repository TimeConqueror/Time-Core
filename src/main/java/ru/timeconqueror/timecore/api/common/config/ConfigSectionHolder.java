package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;

public abstract class ConfigSectionHolder {
    private ArrayList<ConfigSection> sections;

    /**
     * Adds subsection to this holder.
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

    /**
     * Used to provide a comment that can be seen above this section in the config file.
     */
    @Nullable
    public abstract String getComment();

    /**
     * The key of the section. It determines the section in config file and also is used as a part of lang keys.
     */
    @NotNull
    public abstract String getKey();
}
