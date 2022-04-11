package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.animation.component.BoneOption;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.api.util.Empty;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RawAnimation {
    private final LoopMode loopMode;
    private final int animationLength;
    private final List<BoneOption> options;

    public RawAnimation(LoopMode loopMode, int animationLength, List<BoneOption> options) {
        this.loopMode = loopMode;
        this.animationLength = animationLength;
        this.options = options;
    }


    public BasicAnimation bake(ResourceLocation id, String name) {
        Map<String, BoneOption> bones = options.stream().collect(Collectors.toMap(BoneOption::getName, boneOption -> boneOption));
        return new BasicAnimation(loopMode, id, name, animationLength, Collections.unmodifiableMap(bones));
    }

    public static class Deserializer implements JsonDeserializer<RawAnimation> {

        @Override
        public RawAnimation deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = jsonIn.getAsJsonObject();

            LoopMode loopMode = LoopMode.DO_NOT_LOOP;
            if (json.has("loop")) {
                loopMode = context.deserialize(json.get("loop"), LoopMode.class);
            }

            int animationLength = (int) (JSONUtils.getAsFloat(json, "animation_length", 0) * 1000);

            List<BoneOption> boneOptions = new ArrayList<>();

            if (EnvironmentUtils.isOnPhysicalClient() && json.has("bones")) {
                for (Map.Entry<String, JsonElement> boneEntryJson : JSONUtils.getAsJsonObject(json, "bones").entrySet()) {
                    String boneName = boneEntryJson.getKey();
                    JsonObject boneJson = JSONUtils.convertToJsonObject(boneEntryJson.getValue(), boneName);

                    RawBoneOption rawOption = context.deserialize(boneJson, RawBoneOption.class);
                    BoneOption option = rawOption.bake(boneName);
                    boneOptions.add(option);
                }
            }

            return new RawAnimation(loopMode, animationLength, !boneOptions.isEmpty() ? boneOptions : Empty.list());
        }
    }
}
