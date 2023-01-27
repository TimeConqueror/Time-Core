package ru.timeconqueror.timecore.api.client.render.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.client.render.model.InFileLocation;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;
import ru.timeconqueror.timecore.internal.client.handlers.ClientLoadingHandler;

//FIXME javadoc
public class TimeModelAPI {
    public static TimeModelPart bakeRoot(InFileLocation location) {
        return ClientLoadingHandler.MODEL_SET.bakeRoot(location);
    }

    public static InFileLocation registerLocation(ResourceLocation path) {
        return registerLocation(path, InFileLocation.WILDCARD);
    }

    public static InFileLocation registerLocation(ResourceLocation path, @NotNull String modelName) {
        InFileLocation tml = new InFileLocation(path, modelName);
        ClientLoadingHandler.MODEL_SET.regModelLocation(tml);

        return tml;
    }
}
