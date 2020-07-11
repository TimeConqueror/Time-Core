package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TimeModel extends Model {
    private final String name;
    private List<TimeModelRenderer> pieces;
    private Map<String, TimeModelRenderer> pieceMap;

    private float scaleMultiplier = 1F;

    public TimeModel(Function<ResourceLocation, RenderType> renderTypeIn, String name, int textureWidth, int textureHeight) {
        super(renderTypeIn);
        this.name = name;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets custom scale for the model.
     * <p>
     * Should only be called once and before first render frame,
     * otherwise you'll see unexpected render behaviour.
     */
    public TimeModel setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;

        return this;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (pieces != null) {
            matrixStackIn.scale(scaleMultiplier, scaleMultiplier, scaleMultiplier);

            for (TimeModelRenderer piece : pieces) {
                piece.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            matrixStackIn.scale(1 / scaleMultiplier, 1 / scaleMultiplier, 1 / scaleMultiplier);
        }
    }

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

        List<ModelRenderer> children = renderer.childModels;
        if (children != null) {
            for (ModelRenderer child : children) {
                if (child instanceof TimeModelRenderer) {
                    addRendererToMap(((TimeModelRenderer) child));
                }
            }
        }
    }

    @Nullable
    public TimeModelRenderer getPiece(String pieceName) {
        return pieceMap.get(pieceName);
    }
}
