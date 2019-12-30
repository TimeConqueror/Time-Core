package ru.timeconqueror.timecore.api.client.obj.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ObjModelBase extends ModelBase {
    /**
     * Main obj model used for rendering.
     */
    protected AbstractObjModel model;

    public ObjModelBase(AbstractObjModel modelIn) {
        model = modelIn;
    }

    /**
     * Copies the angles from one object to another. This is used when objects should stay aligned with each other, like
     * the hair over a players head.
     */
    public static void copyModelAngles(ObjModelRenderer source, ObjModelRenderer dest) {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }

    /**
     * Copies the angles from one object to another. This is used when objects should stay aligned with each other, like
     * the hair over a players head.
     */
    public static void copyModelAngles(ObjModelRenderer source, ModelRenderer dest) {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }

    /**
     * Copies the angles from one object to another. This is used when objects should stay aligned with each other, like
     * the hair over a players head.
     */
    public static void copyModelAngles(ModelRenderer source, ObjModelRenderer dest) {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        model.renderAll(scale);
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }
}
