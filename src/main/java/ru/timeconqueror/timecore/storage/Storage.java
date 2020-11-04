package ru.timeconqueror.timecore.storage;

import ru.timeconqueror.timecore.storage.settings.ModSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * Storage for all mod settings, which will be used in TimeCore.
 * Also stores all mod-id-dependent features, like LangGenerator.
 */
public class Storage {
    private static final Map<String, ModSettings> ALL_SETTINGS = new HashMap<>();
    private static final Map<String, Features> ALL_FEATURES = new HashMap<>();

    public static ModSettings getSettings(String modId) {
        return ALL_SETTINGS.computeIfAbsent(modId, ModSettings::new);
    }

    public static Features getFeatures(String modId) {
        return ALL_FEATURES.computeIfAbsent(modId, Features::new);
    }
}
