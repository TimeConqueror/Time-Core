package ru.timeconqueror.timecore.client.obj.loader.part;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class ModelObject {
    public String name;
    public ArrayList<Face> faces = new ArrayList<>();
    public int glDrawingMode;

    public ModelObject() {
        this("");
    }

    public ModelObject(String name) {
        this(name, -1);
    }

    public ModelObject(String name, int glDrawingMode) {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @SideOnly(Side.CLIENT)
    public void render(BufferBuilder renderer, float scale) {
        if (faces.size() > 0) {
            for (Face face : faces) {
                face.render(glDrawingMode, renderer, scale);
            }
        }
    }
}
