package com.timeconqueror.timecore.client.objhandler.part;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;

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
    public void render(Tessellator tessellator, float scale) {
        if (faces.size() > 0) {
            for (Face face : faces) {
                face.render(glDrawingMode, tessellator, scale);
            }
        }
    }
}
