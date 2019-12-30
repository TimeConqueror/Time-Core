package ru.timeconqueror.timecore.client.obj.loader.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import ru.timeconqueror.timecore.api.client.obj.model.ObjModelBase;
import ru.timeconqueror.timecore.api.client.obj.model.ObjModelRenderer;
import ru.timeconqueror.timecore.client.obj.loader.ObjModel;

//TODO rework
public class ObjModelQuadruped extends ObjModelBase {
    public ObjModelRenderer head;
    public ObjModelRenderer body;
    public ObjModelRenderer leg1;
    public ObjModelRenderer leg2;
    public ObjModelRenderer leg3;
    public ObjModelRenderer leg4;

    protected float childYOffset = 8.0F;
    protected float childZOffset = 4.0F;

    /**
     * @param head - head object/group name in obj file
     * @param body - body object/group name in obj file
     * @param leg1 - leg1 object/group name in obj file
     * @param leg2 - leg2 object/group name in obj file
     * @param leg3 - leg3 object/group name in obj file
     * @param leg4 - leg4 object/group name in obj file
     */
    public ObjModelQuadruped(ObjModel modelIn, String head, String body, String leg1, String leg2, String leg3, String leg4) {
        super(modelIn);

        initParts(head, body, leg1, leg2, leg3, leg4);
    }

    /**
     * Searches in array of model objects and determines main objects.
     */
    public void initParts(String head, String body, String leg1, String leg2, String leg3, String leg4) {

        for (ObjModelRenderer renderer : model.getParts()) {
            if (renderer.getName().equals(head)) {
                this.head = renderer;
            } else if (renderer.getName().equals(body)) {
                this.body = renderer;
            } else if (renderer.getName().equals(leg1)) {
                this.leg1 = renderer;
            } else if (renderer.getName().equals(leg2)) {
                this.leg2 = renderer;
            } else if (renderer.getName().equals(leg3)) {
                this.leg3 = renderer;
            } else if (renderer.getName().equals(leg4)) {
                this.leg4 = renderer;
            }
        }
    }


    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        if (this.isChild) {
            float f = 2.0F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, this.childYOffset * scale, this.childZOffset * scale);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            this.body.render(scale);
            this.leg1.render(scale);
            this.leg2.render(scale);
            this.leg3.render(scale);
            this.leg4.render(scale);
            GlStateManager.popMatrix();
        } else {
            this.head.render(scale);
            this.body.render(scale);
            this.leg1.render(scale);
            this.leg2.render(scale);
            this.leg3.render(scale);
            this.leg4.render(scale);
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleX = headPitch * 0.017453292F;
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.body.rotateAngleX = ((float) Math.PI / 2F);
        this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
}
