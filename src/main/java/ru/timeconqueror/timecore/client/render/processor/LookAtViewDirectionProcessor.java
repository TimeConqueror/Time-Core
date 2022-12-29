package ru.timeconqueror.timecore.client.render.processor;

import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import ru.timeconqueror.timecore.api.client.render.model.IModelProcessor;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

/**
 * Mimics the behaviour of vanilla model when the head bone should be rotated in the direction of view
 */
public class LookAtViewDirectionProcessor implements IModelProcessor<LivingEntity> {
    private final String headBoneName;

    public LookAtViewDirectionProcessor(String headBoneName) {
        this.headBoneName = headBoneName;
    }

    @Override
    public void process(LivingEntity entity, ITimeModel model, float partialTick) {
        boolean falling = entity.getFallFlyingTicks() > 4;
        boolean swimming = entity.isVisuallySwimming();

        TimeModelPart head = model.getPart(headBoneName);
        Vector3f headRot = head.getRotation();

        float swimAmount = entity.getSwimAmount(partialTick);
        float headPitch = -Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        float bodyYaw = -Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = -Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        float netHeadYaw = headYaw - bodyYaw;
        if (shouldSit && entity.getVehicle() instanceof LivingEntity livingentity) {
            bodyYaw = -Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
            netHeadYaw = headYaw - bodyYaw;
            float f3 = Mth.wrapDegrees(netHeadYaw);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            bodyYaw = headYaw - f3;
            if (f3 * f3 > 2500.0F) {
                bodyYaw += f3 * 0.2F;
            }

            netHeadYaw = headYaw - bodyYaw;
        }

        headRot.add(0, MathUtils.toRadians(netHeadYaw), 0);
        if (falling) {
            headRot.add((float) Math.PI / 4F, 0, 0);
        } else if (swimAmount > 0.0F) {
            if (swimming) {
                headRot.add(this.rotlerpRad(swimAmount, headRot.x(), (float) Math.PI / 4F), 0, 0);
            } else {
                headRot.add(this.rotlerpRad(swimAmount, headRot.x(), MathUtils.toRadians(headPitch)), 0, 0);
            }
        } else {
            headRot.add(MathUtils.toRadians(headPitch), 0, 0);
        }
    }

    private float rotlerpRad(float angleIn, float maxAngleIn, float mulIn) {
        float f = (mulIn - maxAngleIn) % ((float) Math.PI * 2F);
        if (f < -(float) Math.PI) {
            f += ((float) Math.PI * 2F);
        }

        if (f >= (float) Math.PI) {
            f -= ((float) Math.PI * 2F);
        }

        return maxAngleIn + angleIn * f;
    }
}
