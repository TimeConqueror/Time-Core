package ru.timeconqueror.timecore.api.client.render;

import net.minecraft.client.renderer.model.Model;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimeModel extends Model {
    private String name;
    private List<TimeModelRenderer> pieces;
    private Map<String, TimeModelRenderer> pieceMap;

    public TimeModel(String name, int textureWidth, int textureHeight) {
        this.name = name;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public String getName() {
        return name;
    }

    public void render(float scale) {
        if (pieces != null) {
            for (TimeModelRenderer piece : pieces) {
                piece.render(scale);
            }
        }
    }

    public List<TimeModelRenderer> getPieces() {
        return pieces;
    }

    public void setPieces(List<TimeModelRenderer> pieces) {
        this.pieces = pieces;
        pieceMap = pieces.stream().collect(Collectors.toMap(o -> o.boxName, o -> o));
    }

    @Nullable
    public TimeModelRenderer getPiece(String pieceName) {
        return pieceMap.get(pieceName);
    }
}
