package ru.timeconqueror.timecore.storage.settings;

import java.io.File;

public class ModSettings {
    private final String modId;

    private File langGeneratorOutputFile;

    public ModSettings(String modId) {
        this.modId = modId;

        langGeneratorOutputFile = new File("../src/main/resources/assets/" + modId + "/lang/en_us.json");
    }

    public void setLangGeneratorOutputFile(File langGeneratorOutputFile) {
        this.langGeneratorOutputFile = langGeneratorOutputFile;
    }

    public File getLangGeneratorOutputFile() {
        return langGeneratorOutputFile;
    }
}
