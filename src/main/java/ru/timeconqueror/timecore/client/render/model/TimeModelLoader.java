package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;

import java.util.List;
import java.util.function.Function;

public class TimeModelLoader {
    public static final List<TimeModel> BROKEN_MODEL = loadJsonModels(new ResourceLocation(TimeCore.MODID, "models/entity/broken.json"), RenderType::getEntityCutoutNoCull);

    //FIXME remove render type from parser, make it raw model
    public static List<TimeModel> loadJsonModels(ResourceLocation location, Function<ResourceLocation, RenderType> renderType) {
        try {
            return new JsonModelParser().parseJsonModel(location, renderType);
        } catch (Throwable e) {
            TimeCore.LOGGER.error("Can't load model " + location.toString(), e);
        }

        return BROKEN_MODEL;
    }

    public static TimeModel loadJsonModel(ResourceLocation location, Function<ResourceLocation, RenderType> renderType) {
        List<TimeModel> timeModels = loadJsonModels(location, renderType);
        if (timeModels.size() != 1) {
            TimeCore.LOGGER.error("Can't load model " + location.toString() + " due to the file contains more than one model. Use #loadJsonModels method instead.");
            return BROKEN_MODEL.get(0);
        }

        return timeModels.get(0);
    }

    public static <T extends Entity> TimeEntityModel<T> loadJsonEntityModel(ResourceLocation location, Function<ResourceLocation, RenderType> renderType) {
        return new TimeEntityModel<>(loadJsonModel(location, renderType));
    }
}
