package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.ModelItemLocation;
import ru.timeconqueror.timecore.api.client.resource.location.ModelLocation;

public enum StandardItemModelParents {
    DEFAULT(new ModelItemLocation("minecraft", "item/generated")),
    HANDHELD(new ModelItemLocation("minecraft", "item/handheld"));

    private ModelLocation location;

    StandardItemModelParents(ModelLocation location) {
        this.location = location;
    }

    public ModelLocation getModelLocation() {
        return location;
    }
}