package ru.timeconqueror.timecore.api.client.render.model;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.client.render.model.TimeModelPiece;

import java.util.List;

public interface ITimeModel {
    String getName();

    ITimeModel setScaleMultiplier(float scaleMultiplier);

    List<TimeModelPiece> getPieces();

    @Nullable TimeModelPiece getPiece(String pieceName);

    /**
     * Should be called before animation applying & render.
     */
    void reset();
}
