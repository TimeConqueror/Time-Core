package ru.timeconqueror.timecore.client.render.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.resources.IResource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.client.render.JsonParsingException;
import ru.timeconqueror.timecore.util.JsonUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonModelParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.12.0"};

    public List<TimeModel> parseJsonModel(@NotNull ResourceLocation fileLocation, Function<ResourceLocation, RenderType> renderType) throws JsonParsingException {
        try (final IResource resource = Minecraft.getInstance().getResourceManager().getResource(fileLocation)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            JsonObject json = JSONUtils.fromJson(reader, true/*isLenient*/);
            return parseJsonModel(renderType, json);

        } catch (Throwable e) {
            throw new JsonParsingException(e);
        }
    }

    private List<TimeModel> parseJsonModel(Function<ResourceLocation, RenderType> renderType, JsonObject object) throws JsonParsingException {
        List<TimeModel> models = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getKey().equals("format_version")) {
                String formatVersion = entry.getValue().getAsString();
                checkFormatVersion(formatVersion);
            } else {
                TimeModel model = parseSubModel(renderType, entry.getKey(), entry.getValue().getAsJsonArray());
                models.add(model);
            }
        }

        return models;
    }

    private TimeModel parseSubModel(Function<ResourceLocation, RenderType> renderType, String name, JsonArray subModelArr) throws JsonParsingException {
        JsonObject subModel = subModelArr.get(0).getAsJsonObject();
        JsonArray bones = subModel.get("bones").getAsJsonArray();

        JsonObject description = subModel.get("description").getAsJsonObject();
        int textureWidth = JsonUtils.getInt("texture_width", description);
        int textureHeight = JsonUtils.getInt("texture_height", description);

        HashMap<String, RawModelBone> pieces = new HashMap<>();
        for (JsonElement bone : bones) {
            RawModelBone piece = parseBone(bone);
            pieces.put(piece.name, piece);
        }

        List<RawModelBone> rootPieces = new ArrayList<>();
        for (RawModelBone value : pieces.values()) {
            if (value.parentName != null) {
                RawModelBone parent = pieces.get(value.parentName);
                if (parent != null) {
                    if (parent.children == null) parent.children = new ArrayList<>();

                    parent.children.add(value);
                } else {
                    throw new JsonParsingException("Can't find parent node " + value.parentName + " for node " + value.name);
                }
            } else {
                rootPieces.add(value);
            }
        }

        return create(renderType, name, textureWidth, textureHeight, rootPieces);
    }

    private TimeModel create(Function<ResourceLocation, RenderType> renderType, String name, int textureWidth, int textureHeight, List<RawModelBone> rootPieces) {
        TimeModel model = new TimeModel(renderType, name, textureWidth, textureHeight);

        model.setPieces(rootPieces.stream().map(rawModelBone -> rawModelBone.bake(model, null)).collect(Collectors.toList()));

        return model;
    }

    private RawModelBone parseBone(JsonElement bone) throws JsonParsingException {
        Vector3f pivot = JsonUtils.getVec3f("pivot", bone);
        Vector3f rotationAngles = JsonUtils.getVec3f("rotation", bone, new Vector3f(0, 0, 0));
        boolean mirror = JsonUtils.getBoolean("mirror", bone, false);
        boolean neverRender = JsonUtils.getBoolean("neverrender", bone, false);
        float inflate = JsonUtils.getFloat("inflate", bone, 0F);
        String name = JsonUtils.getString("name", bone);
        String parentName = JsonUtils.getString("parent", bone, null);

        List<RawModelBone> extraBones = new ArrayList<>();

        List<RawModelCube> cubes = new ArrayList<>();
        if (bone.getAsJsonObject().has("cubes")) {
            for (JsonElement cube : bone.getAsJsonObject().get("cubes").getAsJsonArray()) {
                Vector3f origin = JsonUtils.getVec3f("origin", cube);
                Vector3f size = JsonUtils.getVec3f("size", cube);
                Vec2f uv = JsonUtils.getVec2f("uv", cube);

                if (cube.getAsJsonObject().has("rotation")) {
                    Vector3f rotation = JsonUtils.getVec3f("rotation", cube);
                    Vector3f innerPivot = JsonUtils.getVec3f("pivot", cube);
                    extraBones.add(new RawModelBone(Lists.newArrayList(new RawModelCube(origin, size, uv)), innerPivot, rotation, false, false, 0F, "cube_wrapper_" + extraBones.size(), name));
                } else {
                    cubes.add(new RawModelCube(origin, size, uv));
                }
            }
        }

        RawModelBone rawModelBone = new RawModelBone(cubes, pivot, rotationAngles, mirror, neverRender, inflate, name, parentName);
        rawModelBone.children = extraBones;
        return rawModelBone;
    }

    private void checkFormatVersion(String version) throws JsonParsingException {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonParsingException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
        }
    }

    public static class RawModelBone {
        private final List<RawModelCube> cubes;
        private final Vector3f pivot;
        private final Vector3f rotationAngles;
        private final boolean mirror;
        private final boolean neverRender;
        private final float inflate;
        private final String name;
        private final String parentName;

        private List<RawModelBone> children;

        private RawModelBone(List<RawModelCube> cubes, Vector3f pivot, Vector3f rotationAngles, boolean mirror, boolean neverRender, float inflate, String name, String parentName) {
            this.cubes = cubes;
            this.pivot = pivot;
            this.rotationAngles = rotationAngles;
            this.mirror = mirror;
            this.neverRender = neverRender;
            this.inflate = inflate;
            this.name = name;
            this.parentName = parentName;
        }

        private TimeModelRenderer bake(TimeModel model, RawModelBone parent) {
            List<TimeModelBox> boxesOut = new ArrayList<>(cubes.size());
            for (RawModelCube cube : cubes) {
                boxesOut.add(cube.bake(model, this));
            }

            TimeModelRenderer renderer = new TimeModelRenderer(model, rotationAngles, name, boxesOut, neverRender);
            if (parent != null) {
                renderer.setRotationPoint(pivot.getX() - parent.pivot.getX(), -(pivot.getY() - parent.pivot.getY()), pivot.getZ() - parent.pivot.getZ());
            } else renderer.setRotationPoint(pivot.getX(), -pivot.getY(), pivot.getZ());

            if (children != null) {
                for (RawModelBone child : children) {
                    renderer.childModels.add(child.bake(model, this));
                }
            }

            return renderer;
        }
    }

    public static class RawModelCube {
        private final Vector3f origin;
        private final Vector3f size;
        private final Vec2f uv;

        private RawModelCube(Vector3f origin, Vector3f size, Vec2f uv) {
            this.origin = origin;
            this.size = size;
            this.uv = uv;
        }

        private TimeModelBox bake(TimeModel model, RawModelBone bone) {
            origin.set(origin.getX() - bone.pivot.getX(), -(origin.getY() + size.getY() - bone.pivot.getY()), origin.getZ() - bone.pivot.getZ());
            return new TimeModelBox(origin, size, uv, bone.inflate, bone.mirror, model.textureWidth, model.textureHeight);
        }
    }
}
