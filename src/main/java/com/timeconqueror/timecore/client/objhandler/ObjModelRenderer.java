package com.timeconqueror.timecore.client.objhandler;

import com.timeconqueror.timecore.client.objhandler.part.ModelObject;
import com.timeconqueror.timecore.client.objhandler.part.Vertex;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
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
    public List<ObjModelRenderer> childModels = new ArrayList<>();
    private ModelObject model;
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
                GL11.glCallList(this.displayList);

                if (this.childModels != null) {
                    for (ObjModelRenderer childModel : this.childModels) {
                        childModel.render(scale);
                    }
                }
            } else {
                GL11.glPushMatrix();

                GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                if (this.rotateAngleZ != 0.0F) {

                    GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GL11.glRotatef(-this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }

                GL11.glTranslatef(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);

                GL11.glCallList(this.displayList);

                if (this.childModels != null) {
                    for (ObjModelRenderer childModel : this.childModels) {
                        childModel.render(scale);
                    }
                }

                GL11.glPopMatrix();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderWithRotation(float scale) {
        if (!this.isHidden) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

            if (this.rotateAngleY != 0.0F) {
                GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
                GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (this.rotateAngleZ != 0.0F) {
                GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            }

            GL11.glTranslatef(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);

            GL11.glCallList(this.displayList);

            if (this.childModels != null) {
                for (ObjModelRenderer childModel : this.childModels) {
                    childModel.render(scale);
                }
            }
            GL11.glPopMatrix();
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
                    GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                }
            } else {
                GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                if (this.rotateAngleZ != 0.0F) {
                    GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
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
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);

        Tessellator tessellator = Tessellator.instance;

        model.render(tessellator, scale);

        GL11.glEndList();
        this.compiled = true;
    }
}
