package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.ItemModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.ModelLocation;

public enum StandardItemModelParents {
    DEFAULT(new ItemModelLocation("minecraft", "item/generated")),
    HANDHELD(new ItemModelLocation("minecraft", "item/handheld")),
    HANDHELD_ROD(new ItemModelLocation("minecraft", "item/handheld_rod"));

    private final ModelLocation location;

    StandardItemModelParents(ModelLocation location) {
        this.location = location;
    }

    public ModelLocation getModelLocation() {
        return location;
    }
}