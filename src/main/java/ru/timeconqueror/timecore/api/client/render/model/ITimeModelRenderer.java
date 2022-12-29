package ru.timeconqueror.timecore.api.client.render.model;

public interface ITimeModelRenderer<T> {
    ITimeModel getTimeModel();

    IModelPuppeteer<T> getPuppeteer();
}
