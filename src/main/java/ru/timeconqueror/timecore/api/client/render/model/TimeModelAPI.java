package ru.timeconqueror.timecore.api.client.render.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;
import ru.timeconqueror.timecore.internal.client.handlers.ClientLoadingHandler;

//FIXME javadoc
public class TimeModelAPI {
    public static TimeModelPart bakeRoot(TimeModelLocation location) {
        return ClientLoadingHandler.MODEL_SET.bakeRoot(location);
    }

    public static TimeModelLocation registerLocation(ResourceLocation path) {
        return registerLocation(path, TimeModelLocation.WILDCARD);
    }

    public static TimeModelLocation registerLocation(ResourceLocation path, @NotNull String modelName) {
        TimeModelLocation tml = new TimeModelLocation(path, modelName);
        ClientLoadingHandler.MODEL_SET.regModelLocation(tml);

        return tml;
    }
}
