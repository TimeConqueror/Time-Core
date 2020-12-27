package ru.timeconqueror.timecore.storage;

import ru.timeconqueror.timecore.api.devtools.gen.lang.LangGeneratorFacade;

public class Features {
    private final String modId;
    private final LangGeneratorFacade langGeneratorFacade = new LangGeneratorFacade();

    public Features(String modId) {
        this.modId = modId;
    }

    public LangGeneratorFacade getLangGeneratorFacade() {
        return langGeneratorFacade;
    }
}
