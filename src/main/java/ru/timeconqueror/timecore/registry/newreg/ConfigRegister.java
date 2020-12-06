package ru.timeconqueror.timecore.registry.newreg;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;
import ru.timeconqueror.timecore.api.common.event.FMLModInitializedEvent;
import ru.timeconqueror.timecore.mod.common.config.TimeCoreConfigManager;
import ru.timeconqueror.timecore.registry.AutoRegistrable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * All {@link TimeRegister}s are used to simplify stuff registering.
 * <p>
 * To use it you need to:
 * <ol>
 *     <li>Create its instance and declare it static. Access modifier can be any.</li>
 *     <li>Attach {@link AutoRegistrable} annotation to it to register it as an event listener.</li>
 *     <li>Extend you main mod class from {@link TimeMod} to enable TimeCore's annotations.</li>
 * </ol>
 *
 * <b>Features:</b>
 * If you need to register stuff, your first step will be to call method #register.
 * If the register system has any extra available registering stuff, then this method will return Register Chain,
 * which will have extra methods to apply.
 * <br>
 * <br>
 * So we add {@link TimeRegister} field to the class as stated above. (with AutoRegistrable annotation, etc.)]
 * <p>
 * One more thing: we should add is a <b>static</b> register method and annotate with {@link AutoRegistrable.InitMethod}. Method can have any access modifier.
 * There we will register all needed stuff, using {@link TimeRegister} field.
 * Method annotated with {@link AutoRegistrable.InitMethod} can have zero parameters or one {@link FMLConstructModEvent} parameter.
 * It will be called before Registry events to prepare all the stuff.
 *
 * <br>
 * <blockquote>
 *     <pre>
 *      public class ConfigRegistryExample {
 *         {@literal @}AutoRegistrable
 *          private static final ConfigRegister REGISTER = new ConfigRegister(TimeCore.MODID);
 *
 *         {@literal @}AutoRegistrable.InitMethod
 *          private static void register() {
 *              REGISTER.register(MainConfig.INSTANCE);
 *          }
 *      }
 *     </pre>
 * </blockquote>
 * <p>
 * <p>
 * Examples can be seen at {@link TimeCoreConfigManager}
 */
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

    private void onInit(FMLModInitializedEvent event) {
        withErrorCatching("FMLModInitializedEvent", () -> {
            runnables.forEach(Runnable::run);
            runnables = null;
        });
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        modEventBus.addListener(this::onLoad);
        modEventBus.addListener(this::onReload);
        modEventBus.addListener(this::onInit);
    }

    private static String format(String path) {
        return path.replace('\\', '/');
    }

    private static void createParentDirs(Path path) {
        File dir = path.toFile().getParentFile();
        if (!dir.exists()) dir.mkdirs();
    }
}
