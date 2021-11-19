package ru.timeconqueror.timecore.api.client.render.model;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

import java.util.List;

/**
 * Why does this interface exist?
 * Well, as all you know, client classes will crash on server side.
 * TimeModel extends vanilla client Model class, so this interface will hide the implementation and make it work on server.
 */
public interface ITimeModel {
    String getName();

    ITimeModel setScaleMultiplier(float scaleMultiplier);

    List<TimeModelRenderer> getPieces();

    @Nullable TimeModelRenderer getPiece(String pieceName);
}
