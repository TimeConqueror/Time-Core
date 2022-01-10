package ru.timeconqueror.timecore.client.render.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.internal.client.handlers.ClientLoadingHandler;

import java.util.Map;
import java.util.function.Function;


public class TimeModel extends Model implements ITimeModel {
    public static final String INTERNAL_ROOT_NAME = "i$root";
    private final TimeModelLocation location;
    private final TimeModelPart root;
    private Map<String, TimeModelPart> partMap;

    private float scaleMultiplier = 1F;

    /**
     * @param renderTypeProvider renderToBuffer type, which determines the settings of how model will be rendered depending on the provided texture location
     */
    public TimeModel(Function<ResourceLocation, RenderType> renderTypeProvider, TimeModelLocation location) {
        super(renderTypeProvider);

        this.location = location;
        this.root = ClientLoadingHandler.MODEL_SET.bakeRoot(location);
        buildPartMap();
    }

    @Override
    public TimeModelLocation getLocation() {
        return location;
    }

    /**
     * Sets custom scale for the model.
     * <p>
     * Should only be called once and before first renderToBuffer frame,
     * otherwise you'll see unexpected renderToBuffer behaviour.
     */
    @Override
    public TimeModel setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;

        return this;
    }

    @Override
    public TimeModelPart getPart(String partName) {
        TimeModelPart part = tryGetPart(partName);
        if (part == null)
            throw new IllegalArgumentException(String.format("Part '%s' was not found in the model '%s'", partName, location));

        return part;
    }

    @Nullable
    public TimeModelPart tryGetPart(String partName) {
        return partMap.get(partName);
    }

    public TimeModelPart getRoot() {
        return root;
    }

    /**
     * Should be called before animation applying & render.
     */
    public void reset() {
        for (TimeModelPart part : partMap.values()) {
            part.reset();
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.scale(scaleMultiplier, scaleMultiplier, scaleMultiplier);
        root.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.scale(1 / scaleMultiplier, 1 / scaleMultiplier, 1 / scaleMultiplier);
    }

    private void buildPartMap() {
        ImmutableMap.Builder<String, TimeModelPart> builder = ImmutableMap.builder();

        addPartToMap(builder, INTERNAL_ROOT_NAME, root);

        this.partMap = builder.build();
    }

    private void addPartToMap(ImmutableMap.Builder<String, TimeModelPart> builder, String name, TimeModelPart part) {
        builder.put(name, part);

        Map<String, TimeModelPart> children = part.getChildren();
        for (Map.Entry<String, TimeModelPart> e : children.entrySet()) {
            addPartToMap(builder, e.getKey(), e.getValue());
        }
    }
}
