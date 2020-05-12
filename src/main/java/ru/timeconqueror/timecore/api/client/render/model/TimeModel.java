package ru.timeconqueror.timecore.api.client.render.model;

import net.minecraft.client.renderer.model.Model;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

import java.util.List;

public class TimeModel extends Model {
    private String name;
    private List<TimeModelRenderer> pieces;

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
    }
}
