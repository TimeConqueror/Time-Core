package ru.timeconqueror.timecore.client.model.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.resources.IResource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.JsonUtils;
import ru.timeconqueror.timecore.client.model.JsonModelContainer;
import ru.timeconqueror.timecore.client.model.TimeModel;
import ru.timeconqueror.timecore.client.model.TimeModelBox;
import ru.timeconqueror.timecore.client.model.TimeModelRenderer;

import javax.vecmath.Vector2f;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class JsonModelParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.12.0"};

    private ResourceLocation fileLocation;
    private HashMap<String, TimeModelRenderer> pieces;

    public JsonModelParser(@NotNull ResourceLocation fileLocation) {
        this.fileLocation = fileLocation;
    }

    public JsonModelContainer parseJsonModel() {
        try (final IResource resource = Minecraft.getInstance().getResourceManager().getResource(fileLocation)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            JsonObject json = JSONUtils.fromJson(reader, true/*isLenient*/);
            return parseJsonModel(json);

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    private JsonModelContainer parseJsonModel(JsonObject object) throws IOException {
        TreeMap<String, TimeModel> models = new TreeMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getKey().equals("format_version")) {
                String formatVersion = entry.getValue().getAsString();
                checkFormatVersion(formatVersion);
            } else {
                TimeModel model = parseSubModel(entry.getKey(), entry.getValue().getAsJsonArray());
                models.put(model.getName(), model);
            }
        }

        return new JsonModelContainer(models);
    }

    private TimeModel parseSubModel(String name, JsonArray subModelArr) throws JsonModelParsingException {
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
                    throw new JsonModelParsingException("Can't find parent node " + value.parentName + " for node " + value.name);
                }
            } else {
                rootPieces.add(value);
            }
        }

        return create(name, textureWidth, textureHeight, rootPieces);
    }

    private TimeModel create(String name, int textureWidth, int textureHeight, List<RawModelBone> rootPieces) {
        TimeModel model = new TimeModel(name, textureWidth, textureHeight);

        model.setPieces(rootPieces.stream().map(rawModelBone -> rawModelBone.bake(model, null)).collect(Collectors.toList()));

        return model;
    }

    private RawModelBone parseBone(JsonElement bone) {
        Vector3f pivot = JsonUtils.getVec3f("pivot", bone);
        Vector3f rotationAngles = JsonUtils.getVec3f("rotation", bone, new Vector3f(0, 0, 0));
        boolean mirror = JsonUtils.getBoolean("mirror", bone, false);
        boolean neverRender = JsonUtils.getBoolean("neverrender", bone, false);
        float inflate = JsonUtils.getFloat("inflate", bone, 0F);
        String name = JsonUtils.getString("name", bone);
        String parentName = JsonUtils.getString("parent", bone, null);

        List<RawModelCube> cubes = new ArrayList<>();
        if (bone.getAsJsonObject().has("cubes")) {
            for (JsonElement cube : bone.getAsJsonObject().get("cubes").getAsJsonArray()) {
                Vector3f origin = JsonUtils.getVec3f("origin", cube);
                Vector3f size = JsonUtils.getVec3f("size", cube);
                Vector2f uv = JsonUtils.getVec2f("uv", cube);

                cubes.add(new RawModelCube(origin, size, uv));
            }
        }

        return new RawModelBone(cubes, pivot, rotationAngles, mirror, neverRender, inflate, name, parentName);
    }

    private void checkFormatVersion(String version) throws JsonModelParsingException {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonModelParsingException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
        }
    }

    public static class RawModelBone {
        private List<RawModelCube> cubes;
        private Vector3f pivot;
        private Vector3f rotationAngles;
        private boolean mirror;
        private boolean neverRender;
        private float inflate;
        private String name;
        private String parentName;

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
//
//            if (parent != null) {
//                pivot.sub(parent.pivot);
//            }

            TimeModelRenderer renderer = new TimeModelRenderer(model, rotationAngles, parentName, boxesOut, neverRender);
            if (parent != null) {
                renderer.setRotationPoint(pivot.getX() - parent.pivot.getX(), -(pivot.getY() - parent.pivot.getY()), pivot.getZ() - parent.pivot.getZ());
            } else renderer.setRotationPoint(pivot.getX(), -pivot.getY(), pivot.getZ());

            if (children != null) {
                renderer.childModels = new ArrayList<>(children.size());
                for (RawModelBone child : children) {
                    renderer.childModels.add(child.bake(model, this));
                }
            }

            return renderer;
        }
    }

    public static class RawModelCube {
        private Vector3f origin;
        private Vector3f size;
        private Vector2f uv;

        private RawModelCube(Vector3f origin, Vector3f size, Vector2f uv) {
            this.origin = origin;
            this.size = size;
            this.uv = uv;
        }

        private TimeModelBox bake(TimeModel model, RawModelBone bone) {
            origin.set(origin.getX() - bone.pivot.getX(), -(origin.getY() + size.getY() - bone.pivot.getY()), origin.getZ() - bone.pivot.getZ());
            return new TimeModelBox(origin, size, uv, bone.inflate, bone.mirror, model.textureWidth, model.textureHeight);
        }
    }

    public static class JsonModelParsingException extends IOException {
        /**
         * Constructs an {@code JsonModelException} with {@code null}
         * as its error detail message.
         */
        public JsonModelParsingException() {
            super();
        }

        /**
         * Constructs an {@code JsonModelException} with the specified detail message.
         *
         * @param message The detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method)
         */
        public JsonModelParsingException(String message) {
            super(message);
        }

        /**
         * Constructs an {@code JsonModelException} with the specified detail message
         * and cause.
         *
         * <p> Note that the detail message associated with {@code cause} is
         * <i>not</i> automatically incorporated into this exception's detail
         * message.
         *
         * @param message The detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method)
         * @param cause   The cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A null value is permitted,
         *                and indicates that the cause is nonexistent or unknown.)
         * @since 1.6
         */
        public JsonModelParsingException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs an {@code JsonModelException} with the specified cause and a
         * detail message of {@code (cause==null ? null : cause.toString())}
         * (which typically contains the class and detail message of {@code cause}).
         * This constructor is useful for IO exceptions that are little more
         * than wrappers for other throwables.
         *
         * @param cause The cause (which is saved for later retrieval by the
         *              {@link #getCause()} method).  (A null value is permitted,
         *              and indicates that the cause is nonexistent or unknown.)
         * @since 1.6
         */
        public JsonModelParsingException(Throwable cause) {
            super(cause);
        }
    }
}
