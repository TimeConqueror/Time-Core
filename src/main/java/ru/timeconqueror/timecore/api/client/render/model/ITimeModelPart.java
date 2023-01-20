package ru.timeconqueror.timecore.api.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.Map;

public interface ITimeModelPart {
    /**
     * Transforms current matrix stack's entry directly to this part.
     * Does NOT require calling the same method from parent parts.
     * WARNING: Can be called only when part's transform is calculated.
     */
    void applyTransform(PoseStack stack);

    /**
     * Returns an immutable view of children map.
     */
    Map<String, TimeModelPart> getChildren();

    Vector3f getTranslation();

    /**
     * Returns rotation of the part in radians.
     */
    Vector3f getRotation();

    Vector3f getScale();

    void reset();
}
