package ru.timeconqueror.timecore.client.render.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.api.client.render.model.IModelProcessor;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.client.render.model.PartLink;

@RequiredArgsConstructor
public class ScaleProcessor<T> implements IModelProcessor<T> {
    private final PartLink partLink;
    @Getter
    private final Vector3f scale;

    public ScaleProcessor(PartLink partLink, float scale) {
        this(partLink, new Vector3f(scale, scale, scale));
    }

    @Override
    public void process(T object, ITimeModel model, float partialTick) {
        partLink.getPart(model).getScale().mul(scale.x(), scale.y(), scale.z());
    }
}
