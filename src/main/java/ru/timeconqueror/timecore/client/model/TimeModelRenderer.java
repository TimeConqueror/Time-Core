package ru.timeconqueror.timecore.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TimeModelRenderer extends RendererModel {
    @Nullable
    private String parentName;
    private List<TimeModelBox> cubes;

    public TimeModelRenderer(TimeModel model, Vector3f pivot, Vector3f rotationAngles, String name, @Nullable String parentName, @NotNull List<TimeModelBox> cubes, boolean mirror, boolean neverRender) {
        super(model, name);
        this.rotationPointX = pivot.getX();
        this.rotationPointY = pivot.getY();
        this.rotationPointZ = pivot.getZ();
        this.rotateAngleX = rotationAngles.getX();
        this.rotateAngleY = rotationAngles.getY();
        this.rotateAngleZ = rotationAngles.getZ();
        this.mirror = mirror;//Whether or not to mirror the texture UV mapping in the x-axis for that cube.
        this.showModel = !neverRender;
        this.parentName = parentName;
        this.cubes = cubes;
        setRotationPoint(0, 0, 0);
    }

    @Override
    public void render(float scale) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(scale);
                }

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                    GlStateManager.callList(this.displayList);
                    if (this.childModels != null) {
                        for (RendererModel childModel : this.childModels) {
                            childModel.render(scale);
                        }
                    }
                } else {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                    if (this.rotateAngleZ != 0.0F) {
                        GlStateManager.rotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F) {
                        GlStateManager.rotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F) {
                        GlStateManager.rotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                    }

                    GlStateManager.translatef(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);

                    GlStateManager.callList(this.displayList);
                    if (this.childModels != null) {
                        for (RendererModel childModel : this.childModels) {
                            childModel.render(scale);
                        }
                    }

                    GlStateManager.popMatrix();
                }
            }
        }
    }

    @Override
    public void renderWithRotation(float scale) {
        super.renderWithRotation(scale);
    }

    @Override
    public void postRender(float scale) {
        super.postRender(scale);
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

    public String getParentName() {
        return parentName;
    }
}
