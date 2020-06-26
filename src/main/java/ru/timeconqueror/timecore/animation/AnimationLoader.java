package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.api.client.render.model.TimeModel;
import ru.timeconqueror.timecore.client.render.animation.Animation;
import ru.timeconqueror.timecore.client.render.animation.JsonAnimationParser;
import ru.timeconqueror.timecore.client.render.model.JsonModelParser;

import java.util.List;

public class AnimationLoader {
    public static final List<TimeModel> BROKEN_MODEL = loadJsonModels(new ResourceLocation(TimeCore.MODID, "models/entity/broken.json"));
    private static List<Animation> brokenAnimation;//TODO

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

    public static List<IAnimation> loadAnimations(ResourceLocation location) {
        try {
            return new JsonAnimationParser().parseAnimations(location);
        } catch (Throwable e) {
            TimeCore.LOGGER.error("Can't load animation " + location.toString(), e);
        }

        throw new RuntimeException();//TODO add empty list with dummy animation
    }

    public static IAnimation loadAnimation(ResourceLocation location) {
        List<IAnimation> iAnimations = loadAnimations(location);

        if (iAnimations.size() != 1) {
            throw new RuntimeException("Can't load animation " + location.toString() + " due to the file contains more than one animation. Use #loadAnimations method instead.");
        }

        return iAnimations.get(0);
    }
}
