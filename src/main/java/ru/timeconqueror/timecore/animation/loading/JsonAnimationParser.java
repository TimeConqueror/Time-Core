package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.animation.component.BoneOption;
import ru.timeconqueror.timecore.animation.component.CatmullRomKeyFrame;
import ru.timeconqueror.timecore.animation.component.KeyFrame;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.JsonUtils;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.ResourceUtils;
import ru.timeconqueror.timecore.client.render.JsonParsingException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JsonAnimationParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.8.0"};

    public Map<String, Animation> parseAnimations(@NotNull ResourceLocation fileLocation) throws JsonParsingException {
        String s = ResourceUtils.asDataSubpath(fileLocation.getNamespace() + "/" + fileLocation.getPath());

        try (final InputStream inputStream = ResourceUtils.getStream(ResourceUtils.asDataSubpath(fileLocation.getNamespace() + "/" + fileLocation.getPath()))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

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

            boolean loop = JSONUtils.getAsBoolean(animationJson, "loop", false);
            int animationLength = (int) (JSONUtils.getAsFloat(animationJson, "animation_length") * 1000);

            List<BoneOption> boneOptions = new ArrayList<>();

            if (FMLEnvironment.dist == Dist.CLIENT) {
                if (animationJson.has("bones")) {
                    for (Map.Entry<String, JsonElement> boneEntryJson : JSONUtils.getAsJsonObject(animationJson, "bones").entrySet()) {
                        String boneName = boneEntryJson.getKey();
                        JsonObject boneJson = JSONUtils.convertToJsonObject(boneEntryJson.getValue(), boneName);
                        BoneOption option = parseAnimationBone(boneName, boneJson);
                        boneOptions.add(option);
                    }
                }
            }

            animationMap.put(name, new BasicAnimation(loop, new ResourceLocation(fileLocation.getNamespace(), fileLocation.getPath() + "/" + name), name, animationLength, !boneOptions.isEmpty() ? Collections.unmodifiableMap(boneOptions.stream().collect(Collectors.toMap(BoneOption::getName, boneOption -> boneOption))) : null));
        }

        return animationMap;
    }

    private BoneOption parseAnimationBone(String boneName, JsonObject boneJson) {
        List<KeyFrame> rotationFrames = parseKeyFrameArr("rotation", boneJson, vec -> {
            vec.set(MathUtils.toRadians(vec.x()), MathUtils.toRadians(vec.y()), MathUtils.toRadians(vec.z()));
        });
        List<KeyFrame> positionFrames = parseKeyFrameArr("position", boneJson, vector3f -> vector3f.setY(-vector3f.y()));

        List<KeyFrame> scaleFrames = parseKeyFrameArr("scale", boneJson, vector3f -> {
        });

        return new BoneOption(boneName, rotationFrames, positionFrames, scaleFrames);
    }

    @Nullable
    private List<KeyFrame> parseKeyFrameArr(String optionName, JsonObject boneJson, Consumer<Vector3f> postProcessor) {
        List<KeyFrame> keyFrames = new ArrayList<>();

        if (boneJson.has(optionName)) {
            JsonElement frameContainerJson = boneJson.get(optionName);
            if (frameContainerJson.isJsonArray()) {
                Vector3f vec = JsonUtils.toVec3f(frameContainerJson.getAsJsonArray(), optionName);
                postProcessor.accept(vec);
                keyFrames.add(new KeyFrame(0, vec));

            } else if (frameContainerJson.isJsonObject()) {
                float prevTime = -1;
                for (Map.Entry<String, JsonElement> keyEntry : frameContainerJson.getAsJsonObject().entrySet()) {
                    String key = keyEntry.getKey();
                    float time = Float.parseFloat(key);

                    JsonElement value = keyEntry.getValue();

                    if (prevTime == -1) {
                        prevTime = time;
                    } else if (time <= prevTime) {
                        throw new JsonSyntaxException("Keyframe with location " + key + " (" + value + ") should have time marker that is bigger than previous. Provided: " + time + ", previous: " + prevTime);
                    }

                    if (value.isJsonObject()) {
                        JsonObject frame = value.getAsJsonObject();
                        String lerpMode = JSONUtils.getAsString(frame, "lerp_mode");
                        Vector3f vec = JsonUtils.getAsVec3f(frame, "post");
                        postProcessor.accept(vec);
                        if (lerpMode.equals("catmullrom")) {
                            keyFrames.add(catmullRomFrame(time, vec));
                        } else if (lerpMode.equals("linear")) {
                            keyFrames.add(frame(time, vec));
                        } else {
                            throw new JsonSyntaxException("Can't parse keyframe with location " + key + " (" + value + "), because it has unknown lerp_mode type: " + lerpMode);
                        }
                    } else if (value.isJsonArray()) {
                        Vector3f vec = JsonUtils.toVec3f(JSONUtils.convertToJsonArray(value, key), key);
                        postProcessor.accept(vec);

                        keyFrames.add(frame(time, vec));
                    } else {
                        throw new JsonSyntaxException("Can't parse keyframe with location " + key + " (" + value + "), because it has unknown type: " + JSONUtils.getType(value));
                    }
                }
            }
        }

        return !keyFrames.isEmpty() ? Collections.unmodifiableList(keyFrames) : null;
    }

    private static KeyFrame frame(float time, Vector3f vec) {
        return new KeyFrame((int) (time * 1000), vec);
    }

    private static KeyFrame catmullRomFrame(float time, Vector3f vec) {
        return new CatmullRomKeyFrame((int) (time * 1000), vec);
    }


    private void checkFormatVersion(String version) {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonSyntaxException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
        }
    }
}