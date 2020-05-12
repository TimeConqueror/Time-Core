package ru.timeconqueror.timecore.client.model;

import net.minecraft.client.renderer.model.Model;

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
        for (TimeModelRenderer piece : pieces) {
            piece.render(scale);
        }
    }

    public void setPieces(List<TimeModelRenderer> pieces) {
        this.pieces = pieces;
    }
}
