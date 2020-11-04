package ru.timeconqueror.timecore.devtools.gen.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LangSection<T> {
    private final HashMap<T, String> entries = new HashMap<>();
    private final String name;
    private final Function<T, String> keyCreator;
    private Comparator<Map.Entry<T, String>> sorter = null;
    private boolean saved = false;

    public LangSection(String name, @NotNull Function<T, String> keyCreator) {
        this.name = name;
        this.keyCreator = keyCreator;
    }

    public LangSection<T> setSortingComparator(@Nullable Comparator<Map.Entry<T, String>> sorter) {
        this.sorter = sorter;

        return this;
    }

    public void addEntry(T entry, String enName) {
        if (saved)
            throw new IllegalStateException("Entry map has already been saved. You should add entries only before they are dumped to file.");
        entries.put(entry, enName);
    }

    public String getComment() {
        return "#" + name;
    }

    public String getName() {
        return name;
    }

    public String createKey(T entry) {
        return keyCreator.apply(entry);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    void sendEntries(Map<String, String> out) {
        entries.entrySet()
                .stream()
                .sorted(sorter != null ? sorter : Comparator.<Map.Entry<T, String>, String>comparing(o -> createKey(o.getKey()).toLowerCase()))
                .forEach(entry -> out.put(createKey(entry.getKey()), entry.getValue()));

        entries.clear();

        saved = true;
    }
}
