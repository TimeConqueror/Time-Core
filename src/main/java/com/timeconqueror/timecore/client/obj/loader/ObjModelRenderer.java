package com.timeconqueror.timecore.client.obj.loader;

import com.timeconqueror.timecore.client.obj.loader.part.ModelObject;
import com.timeconqueror.timecore.client.obj.loader.part.Vertex;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ObjModelRenderer {
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public boolean isHidden;

    private ModelObject model;

    public List<ObjModelRenderer> childModels = new ArrayList<>();
    private ObjModelRaw parent;


    private boolean compiled;
    /**
     * The GL display list rendered by the Tessellator for this model
     */
    private int displayList;


    public ObjModelRenderer(ObjModelRaw parent, ModelObject modelForRender) {
        this.model = modelForRender;
        this.parent = parent;
    }

    public String getName() {
        return model.name;
    }

    public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
        this.rotationPointX = rotationPointXIn;
        this.rotationPointY = rotationPointYIn;
        this.rotationPointZ = rotationPointZIn;
    }

    public void setRotationPoint(Vertex vertex) {
        this.rotationPointX = vertex.x;
        this.rotationPointY = vertex.y;
        this.rotationPointZ = vertex.z;
    }

    /**
     * Adds child to Renderer.
     * After using this method you must call {@link ObjModelRaw#clearDuplications()} method to delete all generated duplicates in {@link #parent}. You may do this after adding all children to the renderer.
     */
    public void addChild(ObjModelRenderer child) {
        childModels.add(child);
        parent.addDuplication(child);
    }

    @SideOnly(Side.CLIENT)
    public void render(float scale) {
        if (!this.isHidden) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                GlStateManager.callList(this.displayList);

                if (this.childModels != null) {
                    for (ObjModelRenderer childModel : this.childModels) {
                        childModel.render(scale);
                    }
                }
            } else {
                GlStateManager.pushMatrix();

                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                if (this.rotateAngleZ != 0.0F) {

                    GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GlStateManager.rotate(-this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }

                GlStateManager.translate(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);

                GlStateManager.callList(this.displayList);

                if (this.childModels != null) {
                    for (ObjModelRenderer childModel : this.childModels) {
                        childModel.render(scale);
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderWithRotation(float scale) {
        if (!this.isHidden) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

            if (this.rotateAngleY != 0.0F) {
                GlStateManager.rotate(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
                GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (this.rotateAngleZ != 0.0F) {
                GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.translate(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);

            GlStateManager.callList(this.displayList);

            if (this.childModels != null) {
                for (ObjModelRenderer childModel : this.childModels) {
                    childModel.render(scale);
                }
            }
            GlStateManager.popMatrix();
        }
    }

    /**
     * Allows the changing of Angles after a box has been rendered
     */
    @SideOnly(Side.CLIENT)
    public void postRender(float scale) {
        if (!this.isHidden) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                    GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                }
            } else {
                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                if (this.rotateAngleZ != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }
            }
        }
    }

    /**
     * Compiles a GL display list for this model
     */
    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float scale) {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(this.displayList, GL11.GL_COMPILE);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

        model.render(bufferbuilder, scale);

        GlStateManager.glEndList();
        this.compiled = true;
    }
}
