package ru.timeconqueror.timecore.animation.renderer;

import ru.timeconqueror.timecore.api.client.render.model.IModelProcessor;
import ru.timeconqueror.timecore.api.client.render.model.IModelPuppeteer;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

import java.util.ArrayList;
import java.util.List;

public class ModelPuppeteer<T> implements IModelPuppeteer<T> {
    private final List<IModelProcessor<? super T>> processors = new ArrayList<>();

    @Override
    public void addModelProcessor(IModelProcessor<? super T> processor) {
        processors.add(processor);
    }

    public void processModel(T object, ITimeModel model, float partialTick) {
        for (IModelProcessor<? super T> processor : processors) {
            processor.process(object, model, partialTick);
        }
    }
}
