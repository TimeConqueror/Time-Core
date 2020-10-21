package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Why does this interface exist?
 * Well, as all you know, client classes will crash on server side.
 * TimeModel extends vanilla client Model class, so this interface will hide the implementation and make it work on server.
 */
public interface ITimeModel {
    String getName();

    ITimeModel setScaleMultiplier(float scaleMultiplier);

    void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);

    List<TimeModelRenderer> getPieces();

    void setPieces(List<TimeModelRenderer> pieces);

    @Nullable TimeModelRenderer getPiece(String pieceName);
}
