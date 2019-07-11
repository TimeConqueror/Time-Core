package ru.timeconqueror.timecore.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.event.OnConfigReloadedEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ConfigReloader {
    private static final Field CONFIGS;

    private static final Field CONFIG_CATEGORIES;
    private static final Field CONFIG_CHILDREN;

    private static final Field CATEGORY_PROPERTIES;

    private static ConfigData configData;

    static {
        CONFIGS = ReflectionHelper.findField(ConfigManager.class, "CONFIGS");
        CONFIGS.setAccessible(true);

        CONFIG_CATEGORIES = ReflectionHelper.findField(Configuration.class, "categories");
        CONFIG_CATEGORIES.setAccessible(true);
        CONFIG_CHILDREN = ReflectionHelper.findField(Configuration.class, "children");
        CONFIG_CHILDREN.setAccessible(true);

        CATEGORY_PROPERTIES = ReflectionHelper.findField(ConfigCategory.class, "properties");
        CATEGORY_PROPERTIES.setAccessible(true);
    }

    /**
     * Reloads config from file for mod with {@code modid}.
     * Works with Config created by annotations from {@link Config}.
     *
     * This method is also fires {@link OnConfigReloadedEvent}, so to reload or process some special things, you may handle this event.
     */
    public static void reloadConfigsFromFile(String modid, String fileName) {
        Map<String, Configuration> configs = null;

        try {
            configs = (Map<String, Configuration>) CONFIGS.get(null);
        } catch (Throwable throwable) {
            TimeCore.logHelper.error("Error while getting CONFIGS map from ConfigManager! Reloading will be canceled!");
            throwable.printStackTrace();
        }

        if (configs != null) {
            File file;
            if (!fileName.endsWith(".cfg")) {
                file = new File(Loader.instance().getConfigDir(), fileName + ".cfg");
            } else {
                file = new File(Loader.instance().getConfigDir(), fileName);
            }

            Configuration tempConfig = configs.get(file.getAbsolutePath());
            if (tempConfig == null) {
                TimeCore.logHelper.error("There is no uploaded file with path: " + file.getAbsolutePath());
                return;
            }

            try {
                saveDefaultValues(tempConfig);

                configs.remove(file.getAbsolutePath());

                ConfigManager.sync(modid, Config.Type.INSTANCE);

                try {
                    configs = (Map<String, Configuration>) CONFIGS.get(null);
                    restoreDefaultValues(configs.get(file.getAbsolutePath()));
                } catch (Throwable e) {
                    configs.put(file.getAbsolutePath(), tempConfig);
                    TimeCore.logHelper.error("Error while switching back default values! Last config data will be restored!");
                    e.printStackTrace();
                    return;
                }

                MinecraftForge.EVENT_BUS.post(new OnConfigReloadedEvent(modid, fileName));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns true, if config from that file exists.
     * <p>{@code fileName} is an relative path from minecraft_folder/config. Must be without extension.
     * <p>Example:
     * <p>fileName = "timecore" -> Path: .../"minecraft_folder"/config/timecore.cfg
     */
    public static boolean hasConfigForFile(String fileName) {
        Map<String, Configuration> configs = null;

        try {
            configs = (Map<String, Configuration>) CONFIGS.get(null);
        } catch (Throwable throwable) {
            TimeCore.logHelper.error("Error while getting CONFIGS map from ConfigManager! Can't check!");
            throwable.printStackTrace();
        }

        if (configs != null) {
            File file;
            if (!fileName.endsWith(".cfg")) {
                file = new File(Loader.instance().getConfigDir(), fileName + ".cfg");
            } else {
                file = new File(Loader.instance().getConfigDir(), fileName);
            }
            System.out.println(file.getAbsolutePath());
            return configs.get(file.getAbsolutePath()) != null;
        }

        return false;
    }

    private static void saveDefaultValues(Configuration config) throws IllegalAccessException {
        configData = new ConfigData(config);
    }

    private static void restoreDefaultValues(Configuration config) throws IllegalAccessException {
        configData.writeData(config);
    }

    private static class ConfigCategoryData {
        private ArrayList<ConfigCategoryData> children = new ArrayList<>();
        private Map<String, PropertyData> properties = new TreeMap<>();

        private ConfigCategoryData(ConfigCategory cat) throws IllegalAccessException {
            readData(cat);
        }

        private void readData(ConfigCategory cat) throws IllegalAccessException {
            cat.getChildren().forEach(child -> {
                try {
                    children.add(new ConfigCategoryData(child));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            Map<String, Property> catProps = (Map<String, Property>) CATEGORY_PROPERTIES.get(cat);
            catProps.forEach((key, prop) -> properties.put(key, new PropertyData(prop)));
        }

        private void writeData(ConfigCategory cat) throws IllegalAccessException {
            ConfigCategory[] childCats = new ConfigCategory[cat.getChildren().size()];
            CastHelper.cast(cat.getChildren().toArray(), childCats);

            for (int i = 0; i < childCats.length; i++) {
                children.get(i).writeData(childCats[i]);
            }

            Map<String, Property> catProps = (Map<String, Property>) CATEGORY_PROPERTIES.get(cat);
            catProps.forEach((key, prop) -> {
                if (prop.isList()) {
                    prop.setDefaultValues(properties.get(key).defaultValues);
                } else {
                    prop.setDefaultValue(properties.get(key).defaultValue);
                }
            });
        }
    }

    private static class ConfigData {
        private Map<String, ConfigCategoryData> categories = new TreeMap<>();
        private Map<String, ConfigData> children = new TreeMap<>();

        private ConfigData(Configuration config) throws IllegalAccessException {
            readData(config);
        }

        private void readData(Configuration config) throws IllegalAccessException {
            Map<String, ConfigCategory> configCategories = (Map<String, ConfigCategory>) CONFIG_CATEGORIES.get(config);
            Map<String, Configuration> configChildren = (Map<String, Configuration>) CONFIG_CHILDREN.get(config);

            configCategories.forEach((key, category) -> {
                try {
                    categories.put(key, new ConfigCategoryData(category));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            configChildren.forEach((key, configObj) -> {
                try {
                    children.put(key, new ConfigData(configObj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }

        private void writeData(Configuration configIn) throws IllegalAccessException {
            Map<String, ConfigCategory> configCategories = (Map<String, ConfigCategory>) CONFIG_CATEGORIES.get(configIn);
            Map<String, Configuration> configChildren = (Map<String, Configuration>) CONFIG_CHILDREN.get(configIn);

            configCategories.forEach((key, category) -> {
                try {
                    categories.get(key).writeData(category);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            configChildren.forEach((key, configObj) -> {
                try {
                    children.get(key).writeData(configObj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static class PropertyData {
        private String defaultValue = null;
        private String[] defaultValues = null;

        private PropertyData(Property prop) {
            if (prop.isList()) {
                defaultValues = prop.getDefaults();
            } else {
                defaultValue = prop.getDefault();
            }
        }
    }
}
