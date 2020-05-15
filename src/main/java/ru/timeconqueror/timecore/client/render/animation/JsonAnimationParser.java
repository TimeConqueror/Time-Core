package ru.timeconqueror.timecore.client.render.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.resources.IResource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.client.JsonParsingException;
import ru.timeconqueror.timecore.util.JsonUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JsonAnimationParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.8.0"};

    public List<Animation> parseAnimations(@NotNull ResourceLocation fileLocation) throws JsonParsingException {
        try (final IResource resource = Minecraft.getInstance().getResourceManager().getResource(fileLocation)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            JsonObject json = JSONUtils.fromJson(reader, true/*isLenient*/);
            return parseAnimation(json);

        } catch (Throwable e) {
            throw new JsonParsingException(e);
        }
    }

    @NotNull
    private List<Animation> parseAnimation(JsonObject object) throws JsonParsingException {
        if (object.has("format_version")) {
            String formatVersion = object.get("format_version").getAsString();
            checkFormatVersion(formatVersion);
        }

        JsonObject animations = JsonUtils.get("animations", object).getAsJsonObject();
        List<Animation> animationList = new ArrayList<>();

        for (Map.Entry<String, JsonElement> animationEntry : animations.entrySet()) {
            String name = animationEntry.getKey();
            JsonElement animationJson = animationEntry.getValue();

            boolean loop = JsonUtils.getBoolean("loop", animationJson, false);
            int animationLength = (int) (JsonUtils.getFloat("animation_length", animationJson) * 1000);

            List<BoneOption> boneOptions = new ArrayList<>();
            JsonElement bones = animationJson.getAsJsonObject().get("bones");
            if (bones != null) {
                for (Map.Entry<String, JsonElement> boneEntryJson : JsonUtils.get("bones", animationJson.getAsJsonObject()).getAsJsonObject().entrySet()) {
                    String boneName = boneEntryJson.getKey();
                    JsonObject boneJson = boneEntryJson.getValue().getAsJsonObject();
                    BoneOption option = parseAnimationBone(boneName, boneJson);
                    boneOptions.add(option);
                }
            }

            animationList.add(new Animation(loop, name, animationLength, !boneOptions.isEmpty() ? boneOptions : null));
        }

        return animationList;
    }

    private BoneOption parseAnimationBone(String boneName, JsonObject boneJson) throws JsonParsingException {
        List<KeyFrame> rotationFrames = parseKeyFrameArr("rotation", boneJson);
        List<KeyFrame> positionFrames = parseKeyFrameArr("position", boneJson);
        List<KeyFrame> scaleFrames = parseKeyFrameArr("scale", boneJson);

        return new BoneOption(boneName, rotationFrames, positionFrames, scaleFrames);
    }

    @Nullable
    private List<KeyFrame> parseKeyFrameArr(String optionName, JsonObject boneJson) throws JsonParsingException {
        JsonObject rotationJson = boneJson.has(optionName) ? boneJson.get(optionName).getAsJsonObject() : null;

        List<KeyFrame> keyFrames = new ArrayList<>();

        float prevTime = -1;
        if (rotationJson != null) {
            for (Map.Entry<String, JsonElement> keyEntry : rotationJson.entrySet()) {
                float time = Float.parseFloat(keyEntry.getKey());

                if (prevTime == -1) {
                    prevTime = time;
                } else if (time <= prevTime) {
                    throw new JsonParsingException("Keyframe with name " + keyEntry.getKey() + " (" + keyEntry.getValue() + ") should have time marker that is bigger than previous. Provided: " + time + ", previous: " + prevTime);
                }

                Vector3f vec = JsonUtils.toVec3f(keyEntry.getValue());
                keyFrames.add(new KeyFrame((int) time * 1000, vec));
            }
        }

        return !keyFrames.isEmpty() ? keyFrames : null;
    }

    private void checkFormatVersion(String version) throws JsonParsingException {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonParsingException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
        }
    }
}