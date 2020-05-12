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

public class JsonModelParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.12.0"};

    private ResourceLocation fileLocation;

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

        TimeModel.Builder modelBuilder = new TimeModel.Builder(name, textureWidth, textureHeight);
        TimeModel model = modelBuilder.retrieve();

        TreeMap<String, TimeModelRenderer> pieces = new TreeMap<>();
        for (JsonElement bone : bones) {
            TimeModelRenderer piece = parseBone(bone, model);
            pieces.put(piece.boxName, piece);
        }

        List<TimeModelRenderer> rootPieces = new ArrayList<>();
        for (TimeModelRenderer value : pieces.values()) {
            if (value.getParentName() != null) {
                TimeModelRenderer parent = pieces.get(value.getParentName());
                if (parent != null) {
                    parent.addChild(value);
                } else {
                    throw new JsonModelParsingException("Can't find parent node " + value.getParentName() + " for node " + value.boxName);
                }
            } else {
                rootPieces.add(value);
            }
        }

        modelBuilder.setRootPieces(rootPieces);

        return model;
    }

    private TimeModelRenderer parseBone(JsonElement bone, TimeModel model) {
        Vector3f pivot = JsonUtils.getVec3f("pivot", bone);
        Vector3f rotationAngles = JsonUtils.getVec3f("rotation", bone, new Vector3f(0, 0, 0));
        boolean mirror = JsonUtils.getBoolean("mirror", bone, false);
        boolean neverRender = JsonUtils.getBoolean("neverrender", bone, false);
        float inflate = JsonUtils.getFloat("inflate", bone, 0F);
        String name = JsonUtils.getString("name", bone);
        String parentName = JsonUtils.getString("parent", bone, null);

        List<TimeModelBox> cubes = new ArrayList<>();
        if (bone.getAsJsonObject().has("cubes")) {
            for (JsonElement cube : bone.getAsJsonObject().get("cubes").getAsJsonArray()) {
                Vector3f origin = JsonUtils.getVec3f("origin", cube);
                Vector3f size = JsonUtils.getVec3f("size", cube);
                Vector2f uv = JsonUtils.getVec2f("uv", cube);

                cubes.add(new TimeModelBox(origin, size, uv, inflate, mirror, model.textureWidth, model.textureHeight));
            }
        }

        return new TimeModelRenderer(model, pivot, rotationAngles, name, parentName, cubes, mirror, neverRender);
    }

    private void checkFormatVersion(String version) throws JsonModelParsingException {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonModelParsingException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
        }
    }

    private static class JsonModelParsingException extends IOException {
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
