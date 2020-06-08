package ru.timeconqueror.timecore.api.client.render.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeModel extends Model {
    private final String name;
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
        this.pieceMap = new HashMap<>();

        for (TimeModelRenderer piece : pieces) {
            addRendererToMap(piece);
        }
    }

    private void addRendererToMap(TimeModelRenderer renderer) {
        pieceMap.put(renderer.boxName, renderer);

        List<RendererModel> children = renderer.childModels;
        if (children != null) {
            for (RendererModel child : children) {
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