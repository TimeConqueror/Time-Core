package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import ru.timeconqueror.timecore.api.common.event.FMLModConstructedEvent;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.api.registry.TimeRegistry;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * The class to register and manage your configs.
 * If you want to use it, you should extend this file and annotate it by {@link TimeAutoRegistrable}
 * with {@link TimeAutoRegistrable.Target#INSTANCE} target.
 */
public abstract class ConfigManager extends TimeRegistry {
    private final ArrayList<Config> CONFIG_LIST = new ArrayList<>();

    private static String validate(String path) {
        return path.replace('\\', '/');
    }

    private static void createParentDirs(Path path) {
        File dir = path.toFile().getParentFile();
        if (!dir.exists()) dir.mkdirs();
    }

    /**
     * Here you should register your configs.
     */
    protected abstract void register();

    /**
     * Registers provided config for your mod.
     * Should be called only in {@link #register()} method.
     */
    public void registerConfig(Config config) {
        String configPath = validate(config.getRelativePath());
        createParentDirs(FMLPaths.CONFIGDIR.get().resolve(configPath));

        ForgeConfigSpec spec = config.setup();
        CONFIG_LIST.add(config);

        ModLoadingContext.get().registerConfig(config.getType(), spec, configPath);
    }

    @SubscribeEvent
    public final void onLoad(final ModConfig.Loading configEvent) {
        CONFIG_LIST.forEach(config -> {
            String path = validate(config.getRelativePath());
            if (path.equals(configEvent.getConfig().getFileName())) {
                config.onEveryLoad(configEvent);
            }
        });
    }

    @SubscribeEvent
    public final void onReload(ModConfig.ConfigReloading configEvent) {
        CONFIG_LIST.forEach(config -> {
            String path = validate(config.getRelativePath());

            if (path.equals(configEvent.getConfig().getFileName())) {
                config.onEveryLoad(configEvent);
            }
        });
    }

    @SubscribeEvent
    public final void onInit(FMLModConstructedEvent event) {
        register();
    }
}
