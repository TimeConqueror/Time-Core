package ru.timeconqueror.timecore.client.model;

import net.minecraft.client.renderer.model.Model;

import java.util.ArrayList;
import java.util.List;

public class TimeModel extends Model {
    private String name;

    private List<TimeModelRenderer> rootPieces;

    public TimeModel(String name, int textureWidth, int textureHeight) {
        this.name = name;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public String getName() {
        return name;
    }

    public void render(float scale) {
        for (TimeModelRenderer rootPiece : rootPieces) {
            rootPiece.render(scale);
        }
    }

    private void setRootPieces(List<TimeModelRenderer> rootPieces) {
        if (this.rootPieces == null) this.rootPieces = new ArrayList<>();
        this.rootPieces.addAll(rootPieces);
    }

    public static class Builder {
        private TimeModel model;

        public Builder(String name, int textureWidth, int textureHeight) {
            this.model = new TimeModel(name, textureWidth, textureHeight);
        }

        public void setRootPieces(List<TimeModelRenderer> rootPieces) {
            model.setRootPieces(rootPieces);
        }

        public TimeModel retrieve() {
            return model;
        }
    }
}
