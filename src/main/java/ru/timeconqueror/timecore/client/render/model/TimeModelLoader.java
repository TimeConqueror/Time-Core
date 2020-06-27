package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.api.client.render.model.TimeModel;

import java.util.List;

public class TimeModelLoader {
    public static final List<TimeModel> BROKEN_MODEL = loadJsonModels(new ResourceLocation(TimeCore.MODID, "models/entity/broken.json"));

    public static List<TimeModel> loadJsonModels(ResourceLocation location) {
        try {
            return new JsonModelParser().parseJsonModel(location);
        } catch (Throwable e) {
            TimeCore.LOGGER.error("Can't load model " + location.toString(), e);
        }

        return BROKEN_MODEL;
    }

    public static TimeModel loadJsonModel(ResourceLocation location) {
        List<TimeModel> timeModels = loadJsonModels(location);
        if (timeModels.size() != 1) {
            TimeCore.LOGGER.error("Can't load model " + location.toString() + " due to the file contains more than one model. Use #loadJsonModels method instead.");
            return BROKEN_MODEL.get(0);
        }

        return timeModels.get(0);
    }

    public static <T extends Entity> TimeEntityModel<T> loadJsonEntityModel(ResourceLocation location) {
        return new TimeEntityModel<>(loadJsonModel(location));
    }
}
