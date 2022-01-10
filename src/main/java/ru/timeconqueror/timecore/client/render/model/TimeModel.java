package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class TimeModel extends Model implements ITimeModel {
    private final String name;
    private List<TimeModelPart> pieces;
    private Map<String, TimeModelPart> pieceMap;

    private float scaleMultiplier = 1F;

    public TimeModel(Function<ResourceLocation, RenderType> renderTypeIn, String name) {
        super(renderTypeIn);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets custom scale for the model.
     * <p>
     * Should only be called once and before first renderToBuffer frame,
     * otherwise you'll see unexpected renderToBuffer behaviour.
     */
    @Override
    public ITimeModel setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;

        return this;
    }

    @Override
    public List<TimeModelPart> getPieces() {
        return pieces;
    }

    public void setPieces(List<TimeModelPart> pieces) {
        this.pieces = pieces;
        this.pieceMap = new HashMap<>();

        for (TimeModelPart piece : pieces) {
            addRendererToMap(piece);
        }
    }

    private void addRendererToMap(TimeModelPart renderer) {
        pieceMap.put(renderer.getName(), renderer);

//        List<ModelPart> children = renderer.children;//FIXME PORT
//        if (children != null) {
//            for (ModelPart child : children) {
//                if (child instanceof TimeModelPiece) {
//                    addRendererToMap(((TimeModelPiece) child));
//                }
//            }
//        }
    }

    @Override
    @Nullable
    public TimeModelPart getPiece(String pieceName) {
        return pieceMap.get(pieceName);
    }

    /**
     * Should be called before animation applying & render.
     */
    public void reset() {
        for (TimeModelPart piece : pieceMap.values()) {
            piece.reset();
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (pieces == null) {
            return;
        }

        matrixStackIn.scale(scaleMultiplier, scaleMultiplier, scaleMultiplier);

        for (TimeModelPart piece : pieces) {
            piece.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }

        matrixStackIn.scale(1 / scaleMultiplier, 1 / scaleMultiplier, 1 / scaleMultiplier);
    }
}
