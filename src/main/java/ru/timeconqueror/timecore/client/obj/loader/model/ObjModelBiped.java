package ru.timeconqueror.timecore.client.obj.loader.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import ru.timeconqueror.timecore.api.client.obj.model.ObjModelBase;
import ru.timeconqueror.timecore.api.client.obj.model.ObjModelRenderer;
import ru.timeconqueror.timecore.client.obj.loader.ObjModel;

//TODO rework
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

    public ObjModelBiped(ObjModel modelIn, String head, String body, String armLeft, String armRight, String legLeft, String legRight) {
        super(modelIn);
        initParts(head, body, armLeft, armRight, legLeft, legRight);
    }

    /**
     * Searches in array of model objects and determines main objects.
     */
    public void initParts(String head, String body, String armLeft, String armRight, String legLeft, String legRight) {
        for (ObjModelRenderer renderer : model.getParts()) {
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
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();

        if (this.isChild) {
            float f = 2.0F;
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            this.body.render(scale);
            this.armRight.render(scale);
            this.armLeft.render(scale);
            this.legRight.render(scale);
            this.legLeft.render(scale);
        } else {
            if (entityIn.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.head.render(scale);
            this.body.render(scale);
            this.armRight.render(scale);
            this.armLeft.render(scale);
            this.legRight.render(scale);
            this.legLeft.render(scale);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    @SuppressWarnings("incomplete-switch")
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getTicksElytraFlying() > 4;
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;

        if (flag) {
            this.head.rotateAngleX = -((float) Math.PI / 4F);
        } else {
            this.head.rotateAngleX = headPitch * 0.017453292F;
        }

        this.body.rotateAngleY = 0.0F;
        float f = 1.0F;

        if (flag) {
            f = (float) (entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ);
            f = f / 0.2F;
            f = f * f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        this.armRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.armLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.armRight.rotateAngleZ = 0.0F;
        this.armLeft.rotateAngleZ = 0.0F;
        this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / f;
        this.legRight.rotateAngleY = 0.0F;
        this.legLeft.rotateAngleY = 0.0F;
        this.legRight.rotateAngleZ = 0.0F;
        this.legLeft.rotateAngleZ = 0.0F;

        if (this.isRiding) {
            this.armRight.rotateAngleX += -((float) Math.PI / 5F);
            this.armLeft.rotateAngleX += -((float) Math.PI / 5F);
            this.legRight.rotateAngleX = -1.4137167F;
            this.legRight.rotateAngleY = ((float) Math.PI / 10F);
            this.legRight.rotateAngleZ = 0.07853982F;
            this.legLeft.rotateAngleX = -1.4137167F;
            this.legLeft.rotateAngleY = -((float) Math.PI / 10F);
            this.legLeft.rotateAngleZ = -0.07853982F;
        }

        this.armRight.rotateAngleY = 0.0F;
        this.armRight.rotateAngleZ = 0.0F;

        this.armRight.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.armLeft.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.armRight.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.armLeft.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
    }

    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);

        if (model instanceof ModelBiped) {
            ModelBiped modelbiped = (ModelBiped) model;
            this.isSneak = modelbiped.isSneak;
        }
    }

    public void setVisible(boolean state) {
        this.head.isHidden = !state;
        this.body.isHidden = !state;
        this.armRight.isHidden = !state;
        this.armLeft.isHidden = !state;
        this.legRight.isHidden = !state;
        this.legLeft.isHidden = !state;
    }

    protected ObjModelRenderer getArmForSide(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? this.armLeft : this.armRight;
    }

    protected EnumHandSide getMainHand(Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase) entityIn;
            EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
            return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
        } else {
            return EnumHandSide.RIGHT;
        }
    }
}
