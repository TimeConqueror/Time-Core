package ru.timeconqueror.timecore.api.util;

import com.mojang.math.Vector3f;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

/**
 * All methods here work on both sides
 */
public class VecUtils {
    /**
     * Coerces {@code direction} vector from region's center within its size.
     * <p>
     * (c) Written by mafs genius Socolio
     */
    public static void cubify(Vector3f direction, float xSize, float ySize, float zSize) {
        float scaleX = (xSize / 2) / Math.abs(direction.x());
        float scaleY = (ySize / 2) / Math.abs(direction.y());
        float scaleZ = (zSize / 2) / Math.abs(direction.z());
        float scale = MathUtils.min(scaleX, scaleY, scaleZ);
        direction.mul(scale, scale, scale);
    }

    /**
     * Coerces {@code direction} vector from region's center within its size.
     * <p>
     * (c) Written by mafs genius Socolio
     */
    public static Vec3 cubify(Vec3 direction, double xSize, double ySize, double zSize) {
        double scaleX = (xSize / 2) / Math.abs(direction.x());
        double scaleY = (ySize / 2) / Math.abs(direction.y());
        double scaleZ = (zSize / 2) / Math.abs(direction.z());
        double scale = MathUtils.min(scaleX, scaleY, scaleZ);
        return direction.scale(scale);
    }

    public static Vec3 vec3d(Vec3i vector3i) {
        return new Vec3(vector3i.getX(), vector3i.getY(), vector3i.getZ());
    }

    public static Vec3 vec3d(Vector3f vector3f) {
        return new Vec3(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public static Vector3f vec3f(Vec3i vector3i) {
        return new Vector3f(vector3i.getX(), vector3i.getY(), vector3i.getZ());
    }

    public static Vec3 add(Vec3 vector3d, Vector3f vector3f) {
        return vector3d.add(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public static Vec3 add(Vec3 vector3d, Vec3i vector3i) {
        return vector3d.add(vector3i.getX(), vector3i.getY(), vector3i.getZ());
    }

    public static Vec3 subtract(Vec3 vector3d, Vector3f vector3f) {
        return vector3d.subtract(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public static Vec3 subtract(Vec3 vector3d, Vec3i vector3i) {
        return vector3d.subtract(vector3i.getX(), vector3i.getY(), vector3i.getZ());
    }

    public static void addToFirst(Vector3f first, Vec3 second) {
        first.set(first.x() + (float) second.x, first.y() + (float) second.y, first.z() + (float) second.z);
    }

    public static void subtractToFirst(Vector3f first, Vec3 second) {
        first.set(first.x() - (float) second.x, first.y() - (float) second.y, first.z() - (float) second.z);
    }

    public static void scaleFirst(Vector3f first, Vector3f second) {
        first.mul(second.x(), second.y(), second.z());
    }

    public static void setFirst(Vector3f first, Vector3f second) {
        first.set(second.x(), second.y(), second.z());
    }
}