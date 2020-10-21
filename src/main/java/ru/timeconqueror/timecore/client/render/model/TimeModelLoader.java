package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimeModelLoader {

    /**
     * Loads single json model to be used in {@link TileEntityRenderer} or smth like that.
     * <font color=yellow>Won't be loaded if file contains multiple models.</font>
     * <p>
     * If you want to load model for an entity, then use {@link #loadJsonEntityModel(ResourceLocation, Function)}
     *
     * @param location           location of file, example: {@code new ResourceLocation(TimeCore.MODID, "models/entity/broken.json")}
     * @param renderTypeProvider renderToBuffer type, which determines the settings of how model will be rendered.
     *                           See static functions in {@link RenderType}.
     * @return single model, which is parsed from the file with provided {@code location}
     * @see #loadJsonEntityModel(ResourceLocation, Function)
     * @see RenderType
     */
    public static TimeModel loadJsonModel(ResourceLocation location, Function<ResourceLocation, RenderType> renderTypeProvider) {
        List<TimeModel> timeModels = loadJsonModels(location, renderTypeProvider);
        if (timeModels.size() != 1) {
            throw new RuntimeException("Can't load model" + location.toString() + " due to the file contains more than one model. Use #loadJsonModels method instead.");
        }

        return timeModels.get(0);
    }

    /**
     * Loads json model list to be used in {@link TileEntityRenderer} or smth like that.
     * Why is this list? That's because file may contain multiple models.
     * <p>
     * If you want to load model for an entity, then use {@link #loadJsonEntityModel(ResourceLocation, Function)}
     *
     * @param location           location of file, example: {@code new ResourceLocation(TimeCore.MODID, "models/entity/broken.json")}
     * @param renderTypeProvider renderToBuffer type, which determines the settings of how model will be rendered.
     *                           See static functions in {@link RenderType}.
     * @return list of models from the file with provided {@code location}
     * @see #loadJsonEntityModel(ResourceLocation, Function)
     * @see RenderType
     */
    public static List<TimeModel> loadJsonModels(ResourceLocation location, Function<ResourceLocation, RenderType> renderTypeProvider) {
        try {
            return new JsonModelParser().parseJsonModel(location)
                    .stream()
                    .map(timeModelFactory -> timeModelFactory.create(renderTypeProvider))
                    .collect(Collectors.toList());
        } catch (Throwable e) {
            throw new RuntimeException("Can't load model " + location.toString(), e);
        }
    }

    /**
     * Loads single json entity model to be used in {@link EntityRenderer}.
     * It binds model to the {@link RenderType#entityCutoutNoCull(ResourceLocation)} renderToBuffer type, which is the default type for living entities.
     * <font color=yellow>Won't be loaded if file contains multiple models.</font>
     *
     * @param location location of file, example: {@code new ResourceLocation(TimeCore.MODID, "models/entity/broken.json")}
     * @return single entity model, which is parsed from the file with provided {@code location}
     * @see #loadJsonEntityModel(ResourceLocation, Function)
     * @see RenderType
     */
    public static <T extends Entity> TimeEntityModel<T> loadJsonEntityModel(ResourceLocation location) {
        return new TimeEntityModel<>(loadJsonModel(location, RenderType::entityCutoutNoCull));
    }

    /**
     * Loads single json entity model to be used in {@link EntityRenderer}.
     * <font color=yellow>Won't be loaded if file contains multiple models.</font>
     *
     * @param location           location of file, example: {@code new ResourceLocation(TimeCore.MODID, "models/entity/broken.json")}
     * @param renderTypeProvider renderToBuffer type, which determines the settings of how model will be rendered.
     *                           See static functions in {@link RenderType}.
     * @return single entity model, which is parsed from the file with provided {@code location}
     * @see #loadJsonEntityModel(ResourceLocation)
     * @see RenderType
     */
    public static <T extends Entity> TimeEntityModel<T> loadJsonEntityModel(ResourceLocation location, Function<ResourceLocation, RenderType> renderTypeProvider) {
        return new TimeEntityModel<>(loadJsonModel(location, renderTypeProvider));
    }
}
