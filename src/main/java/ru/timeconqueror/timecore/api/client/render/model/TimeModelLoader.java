package ru.timeconqueror.timecore.api.client.render.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.client.render.model.parser.JsonModelParser;

import java.util.List;

public class TimeModelLoader {
    private static List<TimeModel> BROKEN = loadJsonModels(new ResourceLocation(TimeCore.MODID, "models/entity/broken.json"));

    public static List<TimeModel> loadJsonModels(ResourceLocation location) {
        try {
            return new JsonModelParser().parseJsonModel(location);
        } catch (JsonModelParser.JsonModelParsingException e) {
            TimeCore.LOGGER.error("Can't load model " + location.toString(), e);
        }

        return BROKEN;
    }

    public static TimeModel loadJsonModel(ResourceLocation location) {
        List<TimeModel> timeModels = loadJsonModels(location);
        if (timeModels.size() != 1) {
            TimeCore.LOGGER.error("Can't load model " + location.toString() + " due to the file contains more than one model. Use TimeModelLoader#loadJsonModels method instead.");
            return BROKEN.get(0);
        }

        return timeModels.get(0);
    }

    public static <T extends Entity> TimeEntityModel<T> loadJsonEntityModel(ResourceLocation location) {
        return new TimeEntityModel<>(loadJsonModel(location));
    }
}
