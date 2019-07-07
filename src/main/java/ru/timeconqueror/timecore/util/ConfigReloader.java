package ru.timeconqueror.timecore.util;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import ru.timeconqueror.timecore.TimeCore;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.util.Map;

public class ConfigReloader {
    private static final MethodHandle CONFIGS_FIELD_GETTER = QuickReflectionHelper.findFieldGetter(ConfigManager.class, "CONFIGS");

    public static void reloadConfigsFromFile(String modid, String fileName) {
        Map<String, Configuration> configs = null;
        try {
            configs = (Map<String, Configuration>) CONFIGS_FIELD_GETTER.invokeExact();
        } catch (Throwable throwable) {
            TimeCore.logHelper.error("Error while getting CONFIGS map from ConfigManager! Reloading will be canceled!");
            throwable.printStackTrace();
        }

        if (configs != null) {
            File file = new File(Loader.instance().getConfigDir(), fileName + ".cfg");
            configs.remove(file.getAbsolutePath());

            ConfigManager.sync(modid, Config.Type.INSTANCE);
        }
    }
}
