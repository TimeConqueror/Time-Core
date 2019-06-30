package com.timeconqueror.timecore.client.objhandler.model;

import com.timeconqueror.timecore.client.objhandler.ObjModelRaw;
import com.timeconqueror.timecore.client.objhandler.ObjModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ObjModelBiped extends ObjModelBase {
    public ObjModelRenderer head;

    public ObjModelRenderer body;
    /**
     * The Biped's Right Arm
     */
    public ObjModelRenderer armRight;
    /**
     * The Biped's Left Arm
     */
    public ObjModelRenderer armLeft;
    /**
     * The Biped's Right Leg
     */
    public ObjModelRenderer legRight;
    /**
     * The Biped's Left Leg
     */
    public ObjModelRenderer legLeft;
    public boolean isSneak;

    public ObjModelBiped(ObjModelRaw modelIn, String head, String body, String armLeft, String armRight, String legLeft, String legRight) {
        super(modelIn);
        initParts(head, body, armLeft, armRight, legLeft, legRight);
    }

    /**
     * Searches in array of model objects and determines main objects.
     */
    public void initParts(String head, String body, String armLeft, String armRight, String legLeft, String legRight) {
        for (ObjModelRenderer renderer : mainModel.parts) {
            if (renderer.getName().equals(head)) {
                this.head = renderer;
            } else if (renderer.getName().equals(body)) {
                this.body = renderer;
            } else if (renderer.getName().equals(armLeft)) {
                this.armLeft = renderer;
            } else if (renderer.getName().equals(armRight)) {
                this.armRight = renderer;
            } else if (renderer.getName().equals(legLeft)) {
                this.legLeft = renderer;
            } else if (renderer.getName().equals(legRight)) {
                this.legRight = renderer;
            }
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        if (this.isChild) {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            this.head.render(scale);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
            this.body.render(scale);
            this.armRight.render(scale);
            this.armLeft.render(scale);
            this.legRight.render(scale);
            this.legLeft.render(scale);
            GL11.glPopMatrix();
        } else {
            this.head.render(scale);
            this.body.render(scale);
            this.armRight.render(scale);
            this.armLeft.render(scale);
            this.legRight.render(scale);
            this.legLeft.render(scale);
        }
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GL11.glPushMatrix();
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    @SuppressWarnings("incomplete-switch")
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
        this.head.rotateAngleX = headPitch / (180F / (float) Math.PI);
        this.armRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.armLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.armRight.rotateAngleZ = 0.0F;
        this.armLeft.rotateAngleZ = 0.0F;
        this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.legRight.rotateAngleY = 0.0F;
        this.legLeft.rotateAngleY = 0.0F;

        if (this.isRiding) {
            this.armRight.rotateAngleX += -((float) Math.PI / 5F);
            this.armLeft.rotateAngleX += -((float) Math.PI / 5F);
            this.legRight.rotateAngleX = -((float) Math.PI * 2F / 5F);
            this.legLeft.rotateAngleX = -((float) Math.PI * 2F / 5F);
            this.legRight.rotateAngleY = ((float) Math.PI / 10F);
            this.legLeft.rotateAngleY = -((float) Math.PI / 10F);
        }

        this.armRight.rotateAngleY = 0.0F;
        this.armLeft.rotateAngleY = 0.0F;

        this.armRight.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.armLeft.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.armRight.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.armLeft.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
    }

    public void setVisible(boolean state) {
        this.head.isHidden = !state;
        this.body.isHidden = !state;
        this.armRight.isHidden = !state;
        this.armLeft.isHidden = !state;
        this.legRight.isHidden = !state;
        this.legLeft.isHidden = !state;
    }
}
