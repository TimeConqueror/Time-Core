package ru.timeconqueror.timecore.api.animation;

import net.minecraft.util.math.vector.Vector3f;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.function.Function;

public enum Channel {
    ROTATION(part -> new Vector3f(0, 0, 0)),
    //TODO 1.18 rename to TRANSLATION
    POSITION(TimeModelPart::getTranslation),
    SCALE(TimeModelPart::getScale);

    private final Function<TimeModelPart, Vector3f> defaultVectorMaker;

    Channel(Function<TimeModelPart, Vector3f> defaultVectorMaker) {
        this.defaultVectorMaker = defaultVectorMaker;
    }

    public Vector3f getDefaultVector(TimeModelPart part) {
        return defaultVectorMaker.apply(part);
    }
}
