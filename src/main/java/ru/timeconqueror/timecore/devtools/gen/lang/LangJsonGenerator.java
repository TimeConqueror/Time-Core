package ru.timeconqueror.timecore.devtools.gen.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.util.Wrapper;
import ru.timeconqueror.timecore.util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LangJsonGenerator {
    private static final String START_MARK = "#MARK AUTO GEN START";
    private static final String END_MARK = "#MARK AUTO GEN END";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
    private static final Marker LOG_MARKER = MarkerManager.getMarker(LangJsonGenerator.class.getSimpleName());

    public void save(String modid, HashMap<String, LangSection<?>> langSectionMap) {
        TimeCore.LOGGER.debug(LOG_MARKER, "Generating of lang entries is started for mod {}", modid);

        File outputFile = new File("../src/main/resources/assets/" + modid + "/lang/en_us.json");
        TimeCore.LOGGER.debug(LOG_MARKER, "The result will be saved at {}", outputFile.getAbsolutePath());

        try {
            FileUtils.prepareFileForRead(outputFile);
            FileUtils.prepareFileForWrite(outputFile);

            Type mapType = new TypeToken<LinkedHashMap<String, String>>() {
            }.getType();
            LinkedHashMap<String, String> entries = GSON.fromJson(new FileReader(outputFile), mapType);
            if (entries == null) {
                entries = new LinkedHashMap<>();
            }

            Wrapper<Integer> index = new Wrapper<>(0);
            Wrapper<Integer> startIndex = new Wrapper<>(-1);
            Wrapper<Integer> endIndex = new Wrapper<>(-1);

            entries.forEach((key, value) -> {
                if (key.equals(START_MARK)) {
                    if (startIndex.get() != -1) {
                        throw new IllegalStateException("Can't handle this file. Found two start index marks in " + (startIndex.get() + 1) + " and " + (index.get() + 1) + "entry");
                    }

                    startIndex.set(index.get());
                } else if (key.equals(END_MARK)) {
                    if (endIndex.get() != -1) {
                        throw new IllegalStateException("Can't handle this file. Found two end index marks in " + (endIndex.get() + 1) + " and " + (index.get() + 1) + "entry");
                    }

                    endIndex.set(index.get());
                }

                index.set(index.get() + 1);
            });


            if (endIndex.get() != -1 && startIndex.get() == -1) {
                throw new IllegalStateException("Can't handle this file. End index is found, but not the start index. " +
                        "End index is in " + (endIndex.get() + 1) + " entry");
            } else if (endIndex.get() != -1 && endIndex.get() < startIndex.get()) {
                throw new IllegalStateException("Can't handle this file. End index is found earlier that start index. " +
                        "End index is in " + (endIndex.get() + 1) + " entry, start index is in " + (startIndex.get() + 1) + " entry ");
            }

            if (startIndex.get() == -1) {
                startIndex.set(entries.size());
            }

            LinkedHashMap<String, String> newValues = new LinkedHashMap<>(entries.size());
            index.set(0);
            Wrapper<Boolean> generated = new Wrapper<>(false);

            LinkedHashMap<String, String> finalEntries = entries;
            entries.forEach((key, value) -> {
                if (index.get() < startIndex.get() || (endIndex.get() != -1 && index.get() >= endIndex.get() + 1)) {
                    newValues.put(key, value);
                }

                if (index.get().equals(startIndex.get()) || (index.get() == finalEntries.size() - 1 && !generated.get())) {
                    newValues.put(START_MARK, "");
                    putAllEntries(langSectionMap, newValues);
                    newValues.put(END_MARK, "");


                    generated.set(true);
                }

                index.set(index.get() + 1);
            });

            try (FileWriter fileWriter = new FileWriter(outputFile)) {
                GSON.toJson(newValues, mapType, fileWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        TimeCore.LOGGER.debug(LOG_MARKER, "Generating of lang entries is finished.");
        TimeCore.LOGGER.debug(LOG_MARKER, "Results were saved in {}", outputFile.getAbsolutePath());
    }

    private void putAllEntries(Map<String, LangSection<?>> sectionMap, Map<String, String> out) {
        sectionMap.forEach((s, langSection) -> {
            if (!langSection.isEmpty()) {
                out.put("_comment", langSection.getComment());
                langSection.sendEntries(out);
            }
        });
    }
}
