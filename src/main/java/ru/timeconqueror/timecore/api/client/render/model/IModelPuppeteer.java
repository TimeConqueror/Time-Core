package ru.timeconqueror.timecore.api.client.render.model;

public interface IModelPuppeteer<T> {
    void addModelProcessor(IModelProcessor<? super T> processor);
}
