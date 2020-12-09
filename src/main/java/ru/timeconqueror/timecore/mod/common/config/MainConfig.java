package ru.timeconqueror.timecore.mod.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;

public class MainConfig extends Config {
    public static final MainConfig INSTANCE = new MainConfig();
    private ForgeConfigSpec.BooleanValue enableDevFeatures;
    public ForgeConfigSpec.BooleanValue suppressExperimentalWarning;

    public MainConfig() {
        super(ModConfig.Type.COMMON, TimeCore.MODID, null);
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {
        enableDevFeatures = builder.comment("Enables development features. Set it to true only if you need to test something, because it may send sensitive server data to clients.")
                .define("enable_dev_features", false);

        suppressExperimentalWarning = builder.comment("If true, suppresses annoying experimental warning, when you're trying to load world with experimental world settings, like new biomes, etc.")
                .define("suppress_experimental_stuff_warning", true);
    }

    public boolean areDevFeaturesEnabled() {
        return enableDevFeatures.get();
    }
}
