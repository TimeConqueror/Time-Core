package ru.timeconqueror.timecore.registry.newreg;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;
import ru.timeconqueror.timecore.api.common.event.FMLModConstructedEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigRegister extends TimeRegister {
    private final ArrayList<Config> CONFIG_LIST = new ArrayList<>();
    private List<Runnable> runnables = new ArrayList<>();

    public ConfigRegister(String modid) {
        super(modid);
    }

    /**
     * Registers provided config for your mod.
     */
    public void register(Config config) {
        String configPath = format(config.getRelativePath());
        createParentDirs(FMLPaths.CONFIGDIR.get().resolve(configPath));

        ImprovedConfigBuilder configBuilder = new ImprovedConfigBuilder(config);
        config.setup(configBuilder);
        ForgeConfigSpec spec = configBuilder.build();

        CONFIG_LIST.add(config);

        runnables.add(() -> ModLoadingContext.get().registerConfig(config.getType(), spec, configPath));
    }


    private void onLoad(final ModConfig.Loading configEvent) {
        CONFIG_LIST.forEach(config -> {
            String path = format(config.getRelativePath());
            if (path.equals(configEvent.getConfig().getFileName())) {
                config.onEveryLoad(configEvent);
            }
        });
    }

    private void onReload(ModConfig.Reloading configEvent) {
        CONFIG_LIST.forEach(config -> {
            String path = format(config.getRelativePath());

            if (path.equals(configEvent.getConfig().getFileName())) {
                config.onEveryLoad(configEvent);
            }
        });
    }

    private void onInit(FMLModConstructedEvent event) {
        withErrorCatching("FMLModConstructedEvent", () -> {
            runnables.forEach(Runnable::run);
            runnables = null;
        });
    }

    @Override
    public void regToBus(IEventBus bus) {
        bus.addListener(this::onLoad);
        bus.addListener(this::onReload);
        bus.addListener(this::onInit);
    }

    private static String format(String path) {
        return path.replace('\\', '/');
    }

    private static void createParentDirs(Path path) {
        File dir = path.toFile().getParentFile();
        if (!dir.exists()) dir.mkdirs();
    }
}
