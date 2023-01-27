package ru.timeconqueror.timecore.api.client.render.model;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.client.render.model.InFileLocation;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

public interface ITimeModel {
    InFileLocation getLocation();

    /**
     * Returns model part by its name or null if there's no model part with provided name.
     */
    @Nullable TimeModelPart tryGetPart(String partName);

    /**
     * Returns model part by its name.
     * Throws {@link IllegalArgumentException} if there's no model part with provided name.
     */
    TimeModelPart getPart(String partName);

    /**
     * Returns the root model part of this model.
     * Root model part is the highest part in hierarchy and contains all other model parts.
     */
    TimeModelPart getRoot();

    /**
     * Should be called before animation applying & render.
     */
    void reset();
}
