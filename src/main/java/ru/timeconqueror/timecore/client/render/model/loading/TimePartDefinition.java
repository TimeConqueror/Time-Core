package ru.timeconqueror.timecore.client.render.model.loading;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.client.render.model.TimeModelCube;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.ArrayList;
import java.util.List;

public class TimePartDefinition {
    private final List<TimeCubeDefinition> cubes;
    private final List<TimePartDefinition> children = new ArrayList<>();
    private final Vector3f pivot;
    private final Vector3f rotationDegrees;
    private final boolean neverRender;
    private final String name;
    private final String parentName;

    public TimePartDefinition(List<TimeCubeDefinition> cubes, Vector3f pivot, Vector3f rotationDegrees, boolean neverRender, String name, String parentName) {
        this.cubes = cubes;
        this.pivot = pivot;
        this.rotationDegrees = rotationDegrees;
        this.neverRender = neverRender;
        this.name = name;
        this.parentName = parentName;
    }

    public static TimePartDefinition makeRoot() {
        return new TimePartDefinition(ImmutableList.of(), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), false, "root", null);
    }

    public void addChild(TimePartDefinition child) {
        this.children.add(child);
    }

    public void addChildren(List<TimePartDefinition> children) {
        this.children.addAll(children);
    }

    public TimeModelPart bake(@Nullable TimePartDefinition parent, int textureWidth, int textureHeight) {
        ImmutableList.Builder<TimeModelCube> bakedCubes = ImmutableList.builder();

        for (TimeCubeDefinition cube : cubes) {
            bakedCubes.add(cube.bake(this, textureWidth, textureHeight));
        }

        Vector3f rotationRads = new Vector3f(MathUtils.toRadians(rotationDegrees.x()),
                MathUtils.toRadians(rotationDegrees.y()),
                MathUtils.toRadians(rotationDegrees.z()));
        rotationRads.mul(-1, -1, 1);

        Object2ObjectArrayMap<String, TimeModelPart> bakedChildren = new Object2ObjectArrayMap<>();
        for (TimePartDefinition child : children) {
            bakedChildren.put(child.name, child.bake(this, textureWidth, textureHeight));
        }

        TimeModelPart part = new TimeModelPart(textureWidth, textureHeight, rotationRads, bakedCubes.build(), bakedChildren, neverRender);
        if (parent != null) {
            part.setPos(-(pivot.x() - parent.pivot.x()), (pivot.y() - parent.pivot.y()), pivot.z() - parent.pivot.z());
        } else {
            part.setPos(-pivot.x(), pivot.y(), pivot.z());
        }

        return part;
    }

    public Vector3f getPivot() {
        return pivot;
    }

    public String getParentName() {
        return parentName;
    }

    public String getName() {
        return name;
    }
}