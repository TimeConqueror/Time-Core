package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.client.render.model.TimeModel;

import java.util.List;

public class TimeModelRenderer extends RendererModel {
    public List<TimeModelBox> cubes;

    public TimeModelRenderer(TimeModel model, Vector3f rotationAngles, String name, @NotNull List<TimeModelBox> cubes, boolean neverRender) {
        super(model, name);
        this.rotateAngleX = rotationAngles.getX();
        this.rotateAngleY = rotationAngles.getY();
        this.rotateAngleZ = rotationAngles.getZ();
        this.showModel = !neverRender;
        this.cubes = cubes;
    }

    @Override
    public void render(float scale) {
        super.render(scale);
    }

    /**
     * Compiles a GL display list for this model
     */
    @Override
    protected void compileDisplayList(float scale) {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.newList(this.displayList, 4864);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

        for (ModelBox modelBox : this.cubeList) {
            modelBox.render(bufferbuilder, scale);
        }

        for (TimeModelBox cube : this.cubes) {
            cube.render(bufferbuilder, scale);
        }

        GlStateManager.endList();
        this.compiled = true;
    }
}
