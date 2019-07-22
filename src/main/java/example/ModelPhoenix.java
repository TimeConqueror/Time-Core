package example;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.obj.ObjModelLoader;
import ru.timeconqueror.timecore.api.client.obj.model.ObjModelBase;
import ru.timeconqueror.timecore.api.client.obj.model.ObjModelRenderer;

public class ModelPhoenix extends ObjModelBase {
    private ObjModelRenderer head;
    private ObjModelRenderer legLeft;
    private ObjModelRenderer legRight;

    public ModelPhoenix() {
        super(ObjModelLoader.load(new ResourceLocation(TimeCore.MODID, "models/entity/phoenix.obj")));

        // Here you can see that I pull some parts out of model to control their rendering.
        // I also add children to other parts in order to not render each thing separately.
        // (why will you do it for every hair on the hair separately for example)
        // This will be useful if you have not grouped the models while creating the obj file.
        //
        // As an example I add "ph_headfeather_small_L" part to "head". So the feather on the head is now part of head and can be rotated as head rotates.
        //
        // WARNING: When you add children to some other parts, be sure to call model#clearDuplications() method
        // after adding all children, as you can see below.
        for (ObjModelRenderer renderer : model.getParts()) {
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

        // WARNING: When(if) you add children to some other parts, be sure to call model#clearDuplications() method
        // after adding all children, as you can see here.
        model.clearDuplications();
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();

        model.renderAll(scale);

        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;

        this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    }


}
