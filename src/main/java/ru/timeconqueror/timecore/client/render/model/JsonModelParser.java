package ru.timeconqueror.timecore.client.render.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.JsonUtils;
import ru.timeconqueror.timecore.client.render.JsonParsingException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class JsonModelParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.12.0"};

    public List<TimeModelFactory> parseJsonModel(@NotNull ResourceLocation fileLocation) throws JsonParsingException {
        try (final IResource resource = Minecraft.getInstance().getResourceManager().getResource(fileLocation)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            JsonObject json = JSONUtils.parse(reader, true);
            return parseJsonModel(json);

        } catch (Throwable e) {
            throw new JsonParsingException(e);
        }
    }

    private List<TimeModelFactory> parseJsonModel(JsonObject object) {
        List<TimeModelFactory> modelFactories = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getKey().equals("format_version")) {
                String formatVersion = JSONUtils.convertToString(entry.getValue(), entry.getKey());
                checkFormatVersion(formatVersion);
            } else {
                TimeModelFactory modelFactory = parseSubModel(entry.getKey(), JSONUtils.convertToJsonArray(entry.getValue(), entry.getKey()));
                modelFactories.add(modelFactory);
            }
        }

        return modelFactories;
    }

    private TimeModelFactory parseSubModel(String name, JsonArray subModelArr) {
        JsonObject subModel = JSONUtils.convertToJsonObject(subModelArr.get(0), "member of " + name + "");
        JsonArray bones = JSONUtils.getAsJsonArray(subModel, "bones");

        JsonObject description = JSONUtils.getAsJsonObject(subModel, "description");
        int textureWidth = JSONUtils.getAsInt(description, "texture_width");
        int textureHeight = JSONUtils.getAsInt(description, "texture_height");

        HashMap<String, RawModelBone> pieces = new HashMap<>();
        for (JsonElement bone : bones) {
            RawModelBone piece = parseBone(JSONUtils.convertToJsonObject(bone, "member of 'bones'"));
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
                    throw new JsonSyntaxException("Can't find parent node " + value.parentName + " for node " + value.name);
                }
            } else {
                rootPieces.add(value);
            }
        }

        return create(name, textureWidth, textureHeight, rootPieces);
    }

    private TimeModelFactory create(String name, int textureWidth, int textureHeight, List<RawModelBone> rootPieces) {
        return renderTypeProvider -> {
            TimeModel model = new TimeModel(renderTypeProvider, name, textureWidth, textureHeight);
            model.setPieces(rootPieces.stream().map(rawModelBone -> rawModelBone.bake(model, null)).collect(Collectors.toList()));

            return model;
        };
    }

    private RawModelBone parseBone(JsonObject bone) {
        Vector3f pivot = JsonUtils.getAsVec3f(bone, "pivot");
        Vector3f rotationAngles = JsonUtils.getAsVec3f(bone, "rotation", new Vector3f(0, 0, 0));
        boolean mirror = JSONUtils.getAsBoolean(bone, "mirror", false);
        boolean neverRender = JSONUtils.getAsBoolean(bone, "neverrender", false);
        float inflate = JSONUtils.getAsFloat(bone, "inflate", 0F);
        String name = JSONUtils.getAsString(bone, "name");
        String parentName = JSONUtils.getAsString(bone, "parent", null);

        List<RawModelBone> extraBones = new ArrayList<>();

        List<RawModelCube> cubes = new ArrayList<>();
        if (bone.has("cubes")) {
            for (JsonElement cube : JSONUtils.getAsJsonArray(bone, "cubes")) {
                JsonObject cubeObject = JSONUtils.convertToJsonObject(cube, "member of 'cubes'");
                Vector3f origin = JsonUtils.getAsVec3f(cubeObject, "origin");
                Vector3f size = JsonUtils.getAsVec3f(cubeObject, "size");
                Vector2f uv = JsonUtils.getAsVec2f(cubeObject, "uv");

                if (cubeObject.has("rotation") || cubeObject.has("inflate") || cubeObject.has("mirror")) {
                    Vector3f rotation = JsonUtils.getAsVec3f(cubeObject, "rotation", new Vector3f(0, 0, 0));
                    Vector3f innerPivot = JsonUtils.getAsVec3f(cubeObject, "pivot", new Vector3f(0, 0, 0));
                    boolean innerMirror = JSONUtils.getAsBoolean(cubeObject, "mirror", false);

                    float cubeInflate = JSONUtils.getAsFloat(cubeObject, "inflate", 0F);
                    extraBones.add(new RawModelBone(Lists.newArrayList(new RawModelCube(origin, size, uv)), innerPivot, rotation, innerMirror, false, cubeInflate, "cube_wrapper_" + extraBones.size(), name));
                } else {
                    cubes.add(new RawModelCube(origin, size, uv));
                }
            }
        }

        RawModelBone rawModelBone = new RawModelBone(cubes, pivot, rotationAngles, mirror, neverRender, inflate, name, parentName);
        rawModelBone.children = extraBones;
        return rawModelBone;
    }

    private void checkFormatVersion(String version) {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonSyntaxException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
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

            Vector3f rotationAnglesRadians = new Vector3f(rotationAngles.x() * (float) Math.PI / 180,
                    rotationAngles.y() * (float) Math.PI / 180,
                    rotationAngles.z() * (float) Math.PI / 180);

            TimeModelRenderer renderer = new TimeModelRenderer(model, rotationAnglesRadians, name, boxesOut, neverRender);
            if (parent != null) {
                renderer.setPos(pivot.x() - parent.pivot.x(), -(pivot.y() - parent.pivot.y()), pivot.z() - parent.pivot.z());
            } else renderer.setPos(pivot.x(), -pivot.y(), pivot.z());

            if (children != null) {
                for (RawModelBone child : children) {
                    renderer.children.add(child.bake(model, this));
                }
            }

            return renderer;
        }
    }

    public static class RawModelCube {
        private final Vector3f origin;
        private final Vector3f size;
        private final Vector2f uv;

        private RawModelCube(Vector3f origin, Vector3f size, Vector2f uv) {
            this.origin = origin;
            this.size = size;
            this.uv = uv;
        }

        private TimeModelBox bake(TimeModel model, RawModelBone bone) {
            origin.set(origin.x() - bone.pivot.x(), -(origin.y() + size.y() - bone.pivot.y()), origin.z() - bone.pivot.z());
            return new TimeModelBox(origin, size, uv, bone.inflate, bone.mirror, model.texWidth, model.texHeight);
        }
    }
}
