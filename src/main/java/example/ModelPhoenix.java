package example;

import com.timeconqueror.timecore.TimeCore;
import com.timeconqueror.timecore.client.objhandler.ObjModelLoader;
import com.timeconqueror.timecore.client.objhandler.ObjModelRenderer;
import com.timeconqueror.timecore.client.objhandler.model.ObjModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ModelPhoenix extends ObjModelBase {
    private ObjModelRenderer head;
    private ObjModelRenderer legLeft;
    private ObjModelRenderer legRight;

    public ModelPhoenix() {
        super(ObjModelLoader.load(new ResourceLocation(TimeCore.MODID, "models/entity/phoenix.obj")));

        for(ObjModelRenderer renderer : mainModel.parts){
            switch (renderer.getName()) {
                case "ph_head":
                    head = renderer;
                    break;
                case "ph_leg_L":
                    legLeft = renderer;
                    break;
                case "ph_leg_R":
                    legRight = renderer;
                    break;
                case "ph_headfeather_small_L":
                    head.addChild(renderer);
                    break;
                case "ph_headfeather_small_R":
                    head.addChild(renderer);
                    break;
                case "ph_beak_bottom":
                    head.addChild(renderer);
                    break;
                case "ph_beak_upper":
                    head.addChild(renderer);
                    break;
                case "ph_headfeather_bigL":
                    head.addChild(renderer);
                    break;
                case "ph_headfeather_bigR":
                    head.addChild(renderer);
                    break;
            }
        }

        mainModel.clearDuplications();
    }

    @Override
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

            mainModel.renderAllExcept(scale, head);
        } else {
            if (entityIn.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            mainModel.renderAll(scale);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;

        this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    }


}
