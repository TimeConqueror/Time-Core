package ru.timeconqueror.timecore.client.render.model.loading;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.resources.IResource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.api.util.Vec2i;
import ru.timeconqueror.timecore.api.util.json.JsonUtils;
import ru.timeconqueror.timecore.client.render.model.FaceUVDefinition;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;
import ru.timeconqueror.timecore.client.render.model.UVDefinition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class JsonModelParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.12.0"};
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Vec2i.class, new Vec2i.JsonAdapter())
            .registerTypeAdapter(FaceUVDefinition.class, new FaceUVDefinition.Deserializer())
            .registerTypeAdapter(UVDefinition.class, new UVDefinition.Deserializer())
            .create();

    /**
     * Loads json model list to be used in {@link EntityRenderer}, {@link TileEntityRenderer}, etc.
     * Why is this list? That's because file may contain multiple models.
     *
     * @param fileLocation location of file, example: {@code new ResourceLocation(TimeCore.MODID, "models/entity/broken.json")}
     * @return list of models from the file with provided {@code location}
     *///FiXME shrink resource location
    public List<Pair<TimeModelLocation, TimeModelDefinition>> parseGeometryFile(@NotNull ResourceLocation fileLocation) {
        try (final IResource resource = Minecraft.getInstance().getResourceManager().getResource(fileLocation)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            JsonObject json = JSONUtils.parse(reader, true);
            return parseGeometryFile(fileLocation, json);

        } catch (Throwable e) {
            throw new RuntimeException("Can't load model file " + fileLocation, e);
        }
    }

    private List<Pair<TimeModelLocation, TimeModelDefinition>> parseGeometryFile(ResourceLocation fileLocation, JsonObject object) {
        List<Pair<TimeModelLocation, TimeModelDefinition>> definitions = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getKey().equals("format_version")) {
                String formatVersion = JSONUtils.convertToString(entry.getValue(), entry.getKey());
                checkFormatVersion(formatVersion);
            } else if (entry.getKey().equals("minecraft:geometry")) {
                Pair<TimeModelLocation, TimeModelDefinition> identifierAndModel = parseGeometry(fileLocation, JSONUtils.convertToJsonArray(entry.getValue(), entry.getKey()));
                definitions.add(identifierAndModel);
            } else {
                throw new JsonSyntaxException("Unrecognized key while parsing json model file: " + entry.getKey());
            }
        }

        return definitions;
    }

    private Pair<TimeModelLocation, TimeModelDefinition> parseGeometry(ResourceLocation fileLocation, JsonArray subModelArr) {
        JsonObject subModel = JSONUtils.convertToJsonObject(subModelArr.get(0), "member of 'minecraft:geometry'");
        JsonArray bones = JSONUtils.getAsJsonArray(subModel, "bones");

        JsonObject description = JSONUtils.getAsJsonObject(subModel, "description");
        String identifier = JSONUtils.getAsString(description, "identifier");
        if (identifier.equals(TimeModelLocation.WILDCARD)) {
            throw new JsonSyntaxException("Found forbidden model identifier: '" + TimeModelLocation.WILDCARD + "'. Change it, the current one is reserved for internal purposes.");
        }

        MaterialDefinition material = new MaterialDefinition(JSONUtils.getAsInt(description, "texture_width"), JSONUtils.getAsInt(description, "texture_height"));

        HashMap<String, TimePartDefinition> parts = new HashMap<>();
        for (JsonElement bone : bones) {
            TimePartDefinition part = parseBone(JSONUtils.convertToJsonObject(bone, "member of 'bones'"));
            parts.put(part.getName(), part);
        }

        List<TimePartDefinition> rootChildren = new ArrayList<>();
        for (TimePartDefinition value : parts.values()) {
            if (!value.getParentName().equals("root")) {
                TimePartDefinition parent = parts.get(value.getParentName());
                if (parent != null) {
                    parent.addChild(value);
                } else {
                    throw new JsonSyntaxException(String.format("Can't find parent node '%s' for node '%s'", value.getParentName(), value.getName()));
                }
            } else {
                rootChildren.add(value);
            }
        }

        return Pair.of(new TimeModelLocation(fileLocation, identifier), makeDefinition(material, rootChildren));

    }

    private TimeModelDefinition makeDefinition(MaterialDefinition material, List<TimePartDefinition> roots) {
        TimeMeshDefinition mesh = new TimeMeshDefinition();
        TimePartDefinition root = mesh.getRoot();
        root.addChildren(roots);

        return TimeModelDefinition.create(mesh, material.getTextureWidth(), material.getTextureHeight());
    }

    private TimePartDefinition parseBone(JsonObject bone) {
        Vector3f pivot = JsonUtils.getAsVec3f(bone, "pivot");
        Vector3f rotation = JsonUtils.getAsVec3f(bone, "rotation", new Vector3f(0, 0, 0));
        boolean mirror = JSONUtils.getAsBoolean(bone, "mirror", false);
        boolean neverRender = JSONUtils.getAsBoolean(bone, "neverRender", false);
        String name = JSONUtils.getAsString(bone, "name");
        String parentName = JSONUtils.getAsString(bone, "parent", "root");

        List<TimePartDefinition> children = new ArrayList<>();

        List<TimeCubeDefinition> cubes = new ArrayList<>();
        if (bone.has("cubes")) {
            int i = 0;
            for (JsonElement cubeJson : JSONUtils.getAsJsonArray(bone, "cubes")) {
                JsonObject cubeObject = JSONUtils.convertToJsonObject(cubeJson, "member of 'cubes'");
                Vector3f origin = JsonUtils.getAsVec3f(cubeObject, "origin");
                Vector3f size = JsonUtils.getAsVec3f(cubeObject, "size");
                UVDefinition uv = GSON.fromJson(cubeObject.get("uv"), UVDefinition.class);
                boolean cubeMirror = JSONUtils.getAsBoolean(cubeObject, "mirror", mirror);
                float inflate = JSONUtils.getAsFloat(cubeObject, "inflate", 0F);

                TimeCubeDefinition cube = new TimeCubeDefinition(origin, size, uv, inflate, cubeMirror);

                if (cubeObject.has("rotation")) {
                    Vector3f innerRotation = JsonUtils.getAsVec3f(cubeObject, "rotation", new Vector3f(0, 0, 0));
                    Vector3f innerPivot = JsonUtils.getAsVec3f(cubeObject, "pivot", new Vector3f(0, 0, 0));

                    children.add(new TimePartDefinition(Collections.singletonList(cube), innerPivot, innerRotation, false, name + "/synth-" + i, name));
                    i++;
                } else {
                    cubes.add(cube);
                }
            }
        }

        TimePartDefinition part = new TimePartDefinition(cubes, pivot, rotation, neverRender, name, parentName);
        part.addChildren(children);
        return part;
    }

    private void checkFormatVersion(String version) {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonSyntaxException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
        }
    }
}
