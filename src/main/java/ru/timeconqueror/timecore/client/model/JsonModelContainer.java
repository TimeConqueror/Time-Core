package ru.timeconqueror.timecore.client.model;

import java.util.Collection;
import java.util.TreeMap;

public class JsonModelContainer {
    private TreeMap<String, TimeModel> models;

    public JsonModelContainer(TreeMap<String, TimeModel> models) {
        this.models = models;
    }

    public TreeMap<String, TimeModel> getModels() {
        return models;
    }

    public TimeModel getByName(String name) {
        return models.get(name);
    }

    public TimeModel asSingleModel() {
        Collection<TimeModel> values = models.values();
        int size = values.size();
        if (size > 2) {
            throw new UnsupportedOperationException("The json model contains " + size + " instead of one model, so it can't be converted.");
        } else if (size == 0) {
            throw new UnsupportedOperationException("The json model doesn't contain any model, so it can't be converted.");
        }

        return values.toArray(new TimeModel[0])[0];
    }
}
