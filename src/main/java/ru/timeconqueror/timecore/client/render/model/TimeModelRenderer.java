package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.client.render.TimeModel;

import java.util.List;

public class TimeModelRenderer extends RendererModel {
    public List<TimeModelBox> cubes;
    public Vector3f startRotationAngles;
    public Vector3f scaleFactor;

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
    public void render(float scale) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(scale);
                }

                GlStateManager.pushMatrix();
                GlStateManager.translatef(this.offsetX, this.offsetY, this.offsetZ);
                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                    if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                        GlStateManager.callList(this.displayList);
                        if (this.childModels != null) {
                            for (RendererModel childModel : this.childModels) {
                                childModel.render(scale);
                            }
                        }
                    } else {
                        GlStateManager.pushMatrix();
                        GlStateManager.translatef(this.rotationPointX * scale * scaleFactor.getX(), this.rotationPointY * scale * scaleFactor.getY(), this.rotationPointZ * scale * scaleFactor.getZ());
                        GlStateManager.callList(this.displayList);
                        if (this.childModels != null) {
                            for (RendererModel childModel : this.childModels) {
                                childModel.render(scale);
                            }
                        }

                        GlStateManager.popMatrix();
                    }
                } else {
                    GlStateManager.pushMatrix();
                    applyRotations(scale);

                    GlStateManager.callList(this.displayList);
                    if (this.childModels != null) {
                        for (RendererModel childModel : this.childModels) {
                            childModel.render(scale);
                        }
                    }

                    GlStateManager.popMatrix();
                }

                GlStateManager.popMatrix();
            }
        }

        resetData();
    }

    @Override
    public void renderWithRotation(float scale) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(scale);
                }

                GlStateManager.pushMatrix();
                applyRotations(scale);

                GlStateManager.callList(this.displayList);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void postRender(float scale) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(scale);
                }

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                    if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                        GlStateManager.translatef(this.rotationPointX * scale * scaleFactor.getX(), this.rotationPointY * scale * scaleFactor.getY(), this.rotationPointZ * scale * scaleFactor.getZ());
                    }
                } else {
                    applyRotations(scale);
                }
            }
        }
    }

    private void applyRotations(float scale) {
        GlStateManager.translatef(this.rotationPointX * scale * scaleFactor.getX(), this.rotationPointY * scale * scaleFactor.getY(), this.rotationPointZ * scale * scaleFactor.getZ());
        if (this.rotateAngleZ != 0.0F) {
            GlStateManager.rotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
        }

        if (this.rotateAngleY != 0.0F) {
            GlStateManager.rotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        }

        if (this.rotateAngleX != 0.0F) {
            GlStateManager.rotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
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
