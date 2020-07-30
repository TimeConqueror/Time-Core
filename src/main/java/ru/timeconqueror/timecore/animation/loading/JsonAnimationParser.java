package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.animation.component.BoneOption;
import ru.timeconqueror.timecore.animation.component.KeyFrame;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.client.render.JsonParsingException;
import ru.timeconqueror.timecore.util.JsonUtils;
import ru.timeconqueror.timecore.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonAnimationParser {
    private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[]{"1.8.0"};

    public Map<String, Animation> parseAnimations(@NotNull ResourceLocation fileLocation) throws JsonParsingException {
        String s = ResourceUtils.asDataSubpath(fileLocation.getNamespace() + "/" + fileLocation.getPath());
        System.out.println(s);

        try (final InputStream inputStream = ResourceUtils.getStream(ResourceUtils.asDataSubpath(fileLocation.getNamespace() + "/" + fileLocation.getPath()))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            JsonObject json = JSONUtils.fromJson(reader, true);
            return parseAnimation(fileLocation, json);

        } catch (Throwable e) {
            throw new JsonParsingException(e);
        }
    }

    @NotNull
    private Map<String, Animation> parseAnimation(ResourceLocation fileLocation, JsonObject object) throws JsonParsingException {
        if (object.has("format_version")) {
            String formatVersion = object.get("format_version").getAsString();
            checkFormatVersion(formatVersion);
        }

        JsonObject animations = JsonUtils.get("animations", object).getAsJsonObject();
        Map<String, Animation> animationMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> animationEntry : animations.entrySet()) {
            String name = animationEntry.getKey();
            JsonElement animationJson = animationEntry.getValue();

            boolean loop = JsonUtils.getBoolean("loop", animationJson, false);
            int animationLength = (int) (JsonUtils.getFloat("animation_length", animationJson) * 1000);

            List<BoneOption> boneOptions = new ArrayList<>();

            if (FMLEnvironment.dist == Dist.CLIENT) {
                JsonElement bones = animationJson.getAsJsonObject().get("bones");
                if (bones != null) {
                    for (Map.Entry<String, JsonElement> boneEntryJson : JsonUtils.get("bones", animationJson.getAsJsonObject()).getAsJsonObject().entrySet()) {
                        String boneName = boneEntryJson.getKey();
                        JsonObject boneJson = boneEntryJson.getValue().getAsJsonObject();
                        BoneOption option = parseAnimationBone(boneName, boneJson);
                        boneOptions.add(option);
                    }
                }
            }

            animationMap.put(name, new BasicAnimation(loop, new ResourceLocation(fileLocation.getNamespace(), fileLocation.getPath() + "/" + name), name, animationLength, !boneOptions.isEmpty() ? Collections.unmodifiableMap(boneOptions.stream().collect(Collectors.toMap(BoneOption::getName, boneOption -> boneOption))) : null));
        }

        return animationMap;
    }

    private BoneOption parseAnimationBone(String boneName, JsonObject boneJson) throws JsonParsingException {
        List<KeyFrame> rotationFrames = parseKeyFrameArr("rotation", boneJson, vec -> {
            vec.set(vec.getX() * (float) Math.PI / 180, vec.getY() * (float) Math.PI / 180, vec.getZ() * (float) Math.PI / 180);
            return vec;
        });
        List<KeyFrame> positionFrames = parseKeyFrameArr("position", boneJson, vector3f -> vector3f);
        if (positionFrames != null) {
            positionFrames.forEach(keyFrame -> keyFrame.getVec().setY(-keyFrame.getVec().getY()));
        }
        List<KeyFrame> scaleFrames = parseKeyFrameArr("scale", boneJson, vector3f -> vector3f);

        return new BoneOption(boneName, rotationFrames, positionFrames, scaleFrames);
    }

    @Nullable
    private List<KeyFrame> parseKeyFrameArr(String optionName, JsonObject boneJson, Function<Vector3f, Vector3f> processor) throws JsonParsingException {
        List<KeyFrame> keyFrames = new ArrayList<>();

        if (boneJson.has(optionName)) {
            JsonElement frameContainerJson = boneJson.get(optionName);
            if (frameContainerJson.isJsonArray()) {
                Vector3f vec = JsonUtils.toVec3f(frameContainerJson);
                vec = processor.apply(vec);
                keyFrames.add(new KeyFrame(0, vec));

            } else if (frameContainerJson.isJsonObject()) {
                float prevTime = -1;
                for (Map.Entry<String, JsonElement> keyEntry : frameContainerJson.getAsJsonObject().entrySet()) {
                    float time = Float.parseFloat(keyEntry.getKey());

                    if (prevTime == -1) {
                        prevTime = time;
                    } else if (time <= prevTime) {
                        throw new JsonParsingException("Keyframe with name " + keyEntry.getKey() + " (" + keyEntry.getValue() + ") should have time marker that is bigger than previous. Provided: " + time + ", previous: " + prevTime);
                    }

                    Vector3f vec = JsonUtils.toVec3f(keyEntry.getValue());
                    vec = processor.apply(vec);

                    keyFrames.add(new KeyFrame((int) (time * 1000), vec));
                }
            }
        }

        return !keyFrames.isEmpty() ? Collections.unmodifiableList(keyFrames) : null;
    }

    private void checkFormatVersion(String version) throws JsonParsingException {
        if (!CollectionUtils.contains(ACCEPTABLE_FORMAT_VERSIONS, version)) {
            throw new JsonParsingException("The format version " + version + " is not supported. Supported versions: " + Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS));
        }
    }
}