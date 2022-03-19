package ru.timeconqueror.timecore.animation.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;

@OnlyIn(Dist.CLIENT)
public abstract class AnimatedEntityRenderer<T extends Entity & AnimatedObject<T>, M extends TimeEntityModel<T>> extends EntityRenderer<T> {
    protected M model;

    public AnimatedEntityRenderer(EntityRendererProvider.Context ctx, M entityModelIn) {
        super(ctx);
        this.model = entityModelIn;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        getModel().reset();

        entity.getSystem().getAnimationManager().applyAnimations(getModel());

        setupAnimations(entity, matrixStack, partialTicks);

        RenderType type = model.renderType(getTextureLocation(entity));
        int rgba = getRGBA(entity);
        model.renderToBuffer(matrixStack, buffer.getBuffer(type), packedLight, packedLight, DrawHelper.getRed(rgba) / 255F, DrawHelper.getGreen(rgba) / 255F, DrawHelper.getBlue(rgba) / 255F, DrawHelper.getAlpha(rgba) / 255F);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected void setupAnimations(T entity, PoseStack matrixStackIn, float partialTicks) {
    }

    public M getModel() {
        return model;
    }

    public int getRGBA(T entity) {
        return 0xFFFFFFFF;
    }
}
