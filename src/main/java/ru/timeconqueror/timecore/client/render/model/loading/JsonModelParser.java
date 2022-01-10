package ru.timeconqueror.timecore.client.render.model.loading;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.JsonUtils;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class JsonModelParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.12.0"};

    /**
     * Loads json model list to be used in {@link BlockEntityRenderer} or smth like that.
     * Why is this list? That's because file may contain multiple models.
     *
     * @param fileLocation location of file, example: {@code new ResourceLocation(TimeCore.MODID, "models/entity/broken.json")}
     * @return list of models from the file with provided {@code location}
     *///FiXME shrink resource location
    public List<Pair<TimeModelLocation, TimeModelDefinition>> parseGeometryFile(@NotNull ResourceLocation fileLocation) {
        try (final Resource resource = Minecraft.getInstance().getResourceManager().getResource(fileLocation)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            JsonObject json = GsonHelper.parse(reader, true);
            return parseGeometryFile(fileLocation, json);

        } catch (Throwable e) {
            throw new RuntimeException("Can't load model file " + fileLocation, e);
        }
    }

    private List<Pair<TimeModelLocation, TimeModelDefinition>> parseGeometryFile(ResourceLocation fileLocation, JsonObject object) {
        List<Pair<TimeModelLocation, TimeModelDefinition>> definitions = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getKey().equals("format_version")) {
                String formatVersion = GsonHelper.convertToString(entry.getValue(), entry.getKey());
                checkFormatVersion(formatVersion);
            } else if (entry.getKey().equals("minecraft:geometry")) {
                Pair<TimeModelLocation, TimeModelDefinition> identifierAndModel = parseGeometry(fileLocation, GsonHelper.convertToJsonArray(entry.getValue(), entry.getKey()));
                definitions.add(identifierAndModel);
            } else {
                throw new JsonSyntaxException("Unrecognized key while parsing json model file: " + entry.getKey());
            }
        }

        return definitions;
    }

    private Pair<TimeModelLocation, TimeModelDefinition> parseGeometry(ResourceLocation fileLocation, JsonArray subModelArr) {
        JsonObject subModel = GsonHelper.convertToJsonObject(subModelArr.get(0), "member of 'minecraft:geometry'");
        JsonArray bones = GsonHelper.getAsJsonArray(subModel, "bones");

        JsonObject description = GsonHelper.getAsJsonObject(subModel, "description");
        String identifier = GsonHelper.getAsString(description, "identifier");

        MaterialDefinition material = new MaterialDefinition(GsonHelper.getAsInt(description, "texture_width"), GsonHelper.getAsInt(description, "texture_height"));

        HashMap<String, TimePartDefinition> parts = new HashMap<>();
        for (JsonElement bone : bones) {
            TimePartDefinition part = parseBone(GsonHelper.convertToJsonObject(bone, "member of 'bones'"));
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

        return TimeModelDefinition.create(mesh, material.textureWidth(), material.textureHeight());
    }

//    private TimeModelFactory create(String name, MaterialDefinition modelCtx, List<TimePartDefinition> rootPieces) {
//        return renderTypeProvider -> {
//            TimeModel model = new TimeModel(renderTypeProvider, name, textureWidth, textureHeight);
//            model.setPieces(rootPieces.stream().map(rawModelBone -> rawModelBone.bake(model, null)).collect(Collectors.toList()));
//
//            return model;
//        };
//    }

    private TimePartDefinition parseBone(JsonObject bone) {
        Vector3f pivot = JsonUtils.getAsVec3f(bone, "pivot");
        Vector3f rotation = JsonUtils.getAsVec3f(bone, "rotation", new Vector3f(0, 0, 0));
        boolean mirror = GsonHelper.getAsBoolean(bone, "mirror", false);
        boolean neverRender = GsonHelper.getAsBoolean(bone, "neverRender", false);
        String name = GsonHelper.getAsString(bone, "name");
        String parentName = GsonHelper.getAsString(bone, "parent", "root");

        List<TimePartDefinition> children = new ArrayList<>();

        List<TimeCubeDefinition> cubes = new ArrayList<>();
        if (bone.has("cubes")) {
            for (JsonElement cubeJson : GsonHelper.getAsJsonArray(bone, "cubes")) {
                JsonObject cubeObject = GsonHelper.convertToJsonObject(cubeJson, "member of 'cubes'");
                Vector3f origin = JsonUtils.getAsVec3f(cubeObject, "origin");
                Vector3f size = JsonUtils.getAsVec3f(cubeObject, "size");
                Vec2 uv = JsonUtils.getAsVec2f(cubeObject, "uv");
                boolean cubeMirror = GsonHelper.getAsBoolean(cubeObject, "mirror", mirror);
                float inflate = GsonHelper.getAsFloat(cubeObject, "inflate", 0F);

                TimeCubeDefinition cube = new TimeCubeDefinition(origin, size, uv, inflate, cubeMirror);

                if (cubeObject.has("rotation")) {
                    Vector3f innerRotation = JsonUtils.getAsVec3f(cubeObject, "rotation", new Vector3f(0, 0, 0));
                    Vector3f innerPivot = JsonUtils.getAsVec3f(cubeObject, "pivot", new Vector3f(0, 0, 0));

                    children.add(new TimePartDefinition(Collections.singletonList(cube), innerPivot, innerRotation, false, "synth_bone_" + children.size(), name));
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

    @FunctionalInterface
    private interface ModelParsingCompleter {
        List<Pair<TimeModelLocation, TimeModelDefinition>> complete(ResourceLocation fileLocation);
    }

    @FunctionalInterface
    private interface DefinitionCompleter {
        Pair<TimeModelLocation, TimeModelDefinition> complete(ResourceLocation fileLocation);
    }
}
