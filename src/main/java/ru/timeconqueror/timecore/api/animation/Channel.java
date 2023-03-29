package ru.timeconqueror.timecore.api.animation;

import com.mojang.math.Vector3f;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.function.Function;

public enum Channel {
    ROTATION(part -> new Vector3f(0, 0, 0)),//TODO getRotation?
    TRANSLATION(TimeModelPart::getTranslation),
    SCALE(TimeModelPart::getScale);

    private final Function<TimeModelPart, Vector3f> defaultVectorMaker;

    Channel(Function<TimeModelPart, Vector3f> defaultVectorMaker) {
        this.defaultVectorMaker = defaultVectorMaker;
    }

    public Vector3f getDefaultVector(TimeModelPart part) {
        return defaultVectorMaker.apply(part);
    }
}
