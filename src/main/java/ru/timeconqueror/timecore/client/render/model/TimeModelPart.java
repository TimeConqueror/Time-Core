package ru.timeconqueror.timecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModelPart;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TimeModelPart extends ModelPart implements ITimeModelPart {
    @Getter
    private final Vector3f translation = new Vector3f();
    /**
     * in radians
     */
    @Getter
    private final Vector3f rotation;
    @Getter
    private final Vector3f scale = new Vector3f(1, 1, 1);
    public Vector3f startRotationRadians;
    private final Map<String, TimeModelPart> children;
    private final List<TimeModelCube> cubes;

    private PoseStack.Pose lastTransform = new PoseStack().last();
    private boolean transformValid;

    public TimeModelPart(Vector3f startRotRadians, @NotNull List<TimeModelCube> cubes, Map<String, TimeModelPart> children, boolean neverRender) {
        super(Collections.emptyList(), Collections.emptyMap());
        startRotationRadians = startRotRadians;
        this.rotation = new Vector3f(startRotRadians);
        this.visible = !neverRender;
        this.children = children;
        this.cubes = cubes;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        transformValid = true;

        if (this.visible) {
            poseStack.pushPose();

            this.translateAndRotate(poseStack);

            lastTransform = poseStack.last();

            this.compile(poseStack.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

            for (TimeModelPart part : this.children.values()) {
                part.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            poseStack.popPose();
        }
    }

    @Override
    public void translateAndRotate(PoseStack matrixStackIn) {
        matrixStackIn.translate(translation.x() / 16F, translation.y() / 16F, translation.z() / 16F);
        matrixStackIn.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);

        if (this.rotation.x != 0.0F || this.rotation.y != 0.0F || this.rotation.z != 0.0F) {
            matrixStackIn.mulPose((new Quaternionf()).rotationZYX(rotation.z, rotation.y, rotation.x));
        }

        if (this.scale.x != 1.0F || this.scale.y != 1.0F || this.scale.z != 1.0F) {
            matrixStackIn.scale(scale.x(), scale.y(), scale.z());
        }
    }

    private void compile(PoseStack.Pose pose, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (TimeModelCube cube : cubes) {
            cube.compile(pose, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    @Override
    public void applyTransform(PoseStack stack) {
        if (!visible) return;

        if (!transformValid) {
            TimeCore.LOGGER.warn("Method #transformTo was called in an inappropriate time. The part's transform is not calculated yet.", new Exception());

            return;
        }

        PoseStack.Pose last = stack.last();
        last.pose().set(lastTransform.pose());
        last.normal().set(lastTransform.normal());
    }

    @Override
    public Vector3f getTranslation() {
        return translation;
    }

    @Override
    public Map<String, TimeModelPart> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    @Override
    public void reset() {
        transformValid = false;
        rotation.set(startRotationRadians.x(), startRotationRadians.y(), startRotationRadians.z());
        translation.set(0, 0, 0);
        scale.set(1, 1, 1);
    }
}
