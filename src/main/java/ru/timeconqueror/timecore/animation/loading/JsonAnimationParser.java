package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.ResourceUtils;
import ru.timeconqueror.timecore.api.util.json.Vector3fJsonAdapter;
import ru.timeconqueror.timecore.client.render.JsonParsingException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JsonAnimationParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.8.0"};
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Vector3f.class, new Vector3fJsonAdapter())
            .registerTypeAdapter(KeyFrameListDeserializer.KEYFRAME_LIST_TYPE, new KeyFrameListDeserializer())
            .registerTypeAdapter(RawBoneOption.class, new RawBoneOption.Deserializer())
            .registerTypeAdapter(RawAnimation.class, new RawAnimation.Deserializer())
            .create();

    public Map<String, Animation> parseAnimations(@NotNull ResourceLocation fileLocation) throws JsonParsingException {
        String path = ResourceUtils.asDataSubpath(fileLocation.getNamespace() + "/" + fileLocation.getPath());

        try (final InputStream in = ResourceUtils.getStream(path)) {
            if (in == null) {
                throw new FileNotFoundException(path);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            JsonObject json = JSONUtils.parse(reader, true);
            return parseAnimation(fileLocation, json);

        } catch (Throwable e) {
            throw new JsonParsingException(e);
        }
    }

    @NotNull
    private Map<String, Animation> parseAnimation(ResourceLocation fileLocation, JsonObject object) {
        if (object.has("format_version")) {
            String formatVersion = object.get("format_version").getAsString();
            checkFormatVersion(formatVersion);
        }

        JsonObject animations = JSONUtils.getAsJsonObject(object, "animations");
        Map<String, Animation> animationMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> animationEntry : animations.entrySet()) {
            String name = animationEntry.getKey();
            JsonObject animationJson = JSONUtils.convertToJsonObject(animationEntry.getValue(), name);
            name = name.toLowerCase(Locale.ROOT);

            RawAnimation rawAnimation = GSON.fromJson(animationJson, RawAnimation.class);
            BasicAnimation baked = rawAnimation.bake(new ResourceLocation(fileLocation.getNamespace(), fileLocation.getPath() + "/" + name), name);

            animationMap.put(name, baked);
        }

        return animationMap;
    }

    private void checkFormatVersion(String version) {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonSyntaxException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
        }
    }
}