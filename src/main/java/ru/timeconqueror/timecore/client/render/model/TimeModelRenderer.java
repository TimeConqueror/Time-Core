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
    private final Vector3f scaleFactor = new Vector3f(1, 1, 1);
    public List<TimeModelBox> cubes;
    public Vector3f startRotationAngles;

    public TimeModelRenderer(TimeModel model, Vector3f rotationAngles, String name, @NotNull List<TimeModelBox> cubes, boolean neverRender) {
        super(model, name);
        startRotationAngles = rotationAngles;
        this.rotateAngleX = rotationAngles.getX();
        this.rotateAngleY = rotationAngles.getY();
        this.rotateAngleZ = rotationAngles.getZ();
        this.showModel = !neverRender;
        this.cubes = cubes;
    }

    @Override
    public void render(float initialScale) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(initialScale);
                }

                GlStateManager.pushMatrix();

                GlStateManager.translatef(this.offsetX * initialScale * scaleFactor.getX(), this.offsetY * initialScale * scaleFactor.getY(), this.offsetZ * initialScale * scaleFactor.getZ());

                applyRotations(initialScale);
                draw(initialScale);

                GlStateManager.popMatrix();
            }
        }

        resetData();
    }

    @Override
    public void renderWithRotation(float initialScale) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(initialScale);
                }

                GlStateManager.pushMatrix();
                applyRotations(initialScale);

                GlStateManager.callList(this.displayList);
                GlStateManager.popMatrix();
            }
        }
    }

    private void draw(float initialScale) {
        GlStateManager.scalef(scaleFactor.getX(), scaleFactor.getY(), scaleFactor.getZ());
        GlStateManager.callList(this.displayList);
        if (this.childModels != null) {
            for (RendererModel childModel : this.childModels) {
                childModel.render(initialScale);
            }
        }
        GlStateManager.scalef(1 / scaleFactor.getX(), 1 / scaleFactor.getY(), 1 / scaleFactor.getZ());
    }

    @Override
    public void postRender(float initialScale) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(initialScale);
                }

                applyRotations(initialScale);
            }
        }
    }

    private void applyRotations(float initialScale) {
        if (rotationPointX != 0 || rotationPointY != 0 || rotationPointZ != 0) {
            GlStateManager.translatef(this.rotationPointX * initialScale, this.rotationPointY * initialScale, this.rotationPointZ * initialScale);
        }

        if (this.rotateAngleZ != 0.0F) {
            GlStateManager.rotatef(this.rotateAngleZ, 0.0F, 0.0F, 1.0F);
        }

        if (this.rotateAngleY != 0.0F) {
            GlStateManager.rotatef(this.rotateAngleY, 0.0F, 1.0F, 0.0F);
        }

        if (this.rotateAngleX != 0.0F) {
            GlStateManager.rotatef(this.rotateAngleX, 1.0F, 0.0F, 0.0F);
        }
    }

    private void resetData() {
        rotateAngleX = startRotationAngles.getX();
        rotateAngleY = startRotationAngles.getY();
        rotateAngleZ = startRotationAngles.getZ();

        offsetX = 0;
        offsetY = 0;
        offsetZ = 0;

        scaleFactor.set(1, 1, 1);
    }

    /**
     * Compiles a GL display list for this model
     */
    @Override
    protected void compileDisplayList(float initialScale) {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.newList(this.displayList, 4864);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

        for (ModelBox modelBox : this.cubeList) {
            modelBox.render(bufferbuilder, initialScale);
        }

        for (TimeModelBox cube : this.cubes) {
            cube.render(bufferbuilder, initialScale);
        }

        GlStateManager.endList();
        this.compiled = true;
    }

    public void setScaleFactor(float scaleX, float scaleY, float scaleZ) {
        this.scaleFactor.set(scaleX, scaleY, scaleZ);
    }

    public Vector3f getScaleFactor() {
        return scaleFactor;
    }
}
