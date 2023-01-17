package ru.timeconqueror.timecore.client.render.processor;

import com.mojang.math.Vector3f;
import ru.timeconqueror.timecore.api.client.render.model.IModelProcessor;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

public class ScaleProcessor<T> implements IModelProcessor<T> {
    private final Vector3f scale;

    public ScaleProcessor(Vector3f scale) {
        this.scale = scale;
    }

    public ScaleProcessor(float scale) {
        this.scale = new Vector3f(scale, scale, scale);
    }

    @Override
    public void process(T object, ITimeModel model, float partialTick) {
        model.getRoot().getScale().mul(scale.x(), scale.y(), scale.z());
    }

    public Vector3f getScale() {
        return scale;
    }
}
