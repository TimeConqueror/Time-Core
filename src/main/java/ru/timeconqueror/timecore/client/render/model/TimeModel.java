package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class TimeModel extends Model implements ITimeModel {
    private final String name;
    private List<TimeModelRenderer> pieces;
    private Map<String, TimeModelRenderer> pieceMap;

    private float scaleMultiplier = 1F;

    public TimeModel(Function<ResourceLocation, RenderType> renderTypeIn, String name, int textureWidth, int textureHeight) {
        super(renderTypeIn);
        this.name = name;
        this.texWidth = textureWidth;
        this.texHeight = textureHeight;
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
    public List<TimeModelRenderer> getPieces() {
        return pieces;
    }

    public void setPieces(List<TimeModelRenderer> pieces) {
        this.pieces = pieces;
        this.pieceMap = new HashMap<>();

        for (TimeModelRenderer piece : pieces) {
            addRendererToMap(piece);
        }
    }

    private void addRendererToMap(TimeModelRenderer renderer) {
        pieceMap.put(renderer.getName(), renderer);

        List<ModelRenderer> children = renderer.children;
        if (children != null) {
            for (ModelRenderer child : children) {
                if (child instanceof TimeModelRenderer) {
                    addRendererToMap(((TimeModelRenderer) child));
                }
            }
        }
    }

    @Override
    public TimeModelRenderer getPiece(String pieceName) {
        TimeModelRenderer part = tryGetPiece(pieceName);
        if (part == null)
            throw new IllegalArgumentException(String.format("Part '%s' was not found in the model '%s'", pieceName, name));

        return part;
    }

    @Override
    @Nullable
    public TimeModelRenderer tryGetPiece(String pieceName) {
        return pieceMap.get(pieceName);
    }

    /**
     * Should be called before animation applying & render.
     */
    public void reset() {
        for (TimeModelRenderer piece : pieceMap.values()) {
            piece.reset();
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (pieces == null) {
            return;
        }

        matrixStackIn.scale(scaleMultiplier, scaleMultiplier, scaleMultiplier);

        for (TimeModelRenderer piece : pieces) {
            piece.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }

        matrixStackIn.scale(1 / scaleMultiplier, 1 / scaleMultiplier, 1 / scaleMultiplier);
    }
}
