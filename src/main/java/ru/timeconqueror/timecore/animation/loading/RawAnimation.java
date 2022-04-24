package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.animation.component.BoneOption;
import ru.timeconqueror.timecore.animation.util.Empty;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RawAnimation {
    private final boolean loop;
    private final int animationLength;
    private final List<BoneOption> options;

    public RawAnimation(boolean loop, int animationLength, List<BoneOption> options) {
        this.loop = loop;
        this.animationLength = animationLength;
        this.options = options;
    }

    public BasicAnimation bake(ResourceLocation id, String name) {
        Map<String, BoneOption> bones = options.stream().collect(Collectors.toMap(BoneOption::getName, boneOption -> boneOption));
        return new BasicAnimation(loop, id, name, animationLength, Collections.unmodifiableMap(bones));
    }

    public static class Deserializer implements JsonDeserializer<RawAnimation> {

        @Override
        public RawAnimation deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = jsonIn.getAsJsonObject();
            boolean loop = GsonHelper.getAsBoolean(json, "loop", false);
            int animationLength = (int) (GsonHelper.getAsFloat(json, "animation_length", 0) * 1000);

            List<BoneOption> boneOptions = new ArrayList<>();

            if (EnvironmentUtils.isOnPhysicalClient() && json.has("bones")) {
                for (Map.Entry<String, JsonElement> boneEntryJson : GsonHelper.getAsJsonObject(json, "bones").entrySet()) {
                    String boneName = boneEntryJson.getKey();
                    JsonObject boneJson = GsonHelper.convertToJsonObject(boneEntryJson.getValue(), boneName);

                    RawBoneOption rawOption = context.deserialize(boneJson, RawBoneOption.class);
                    BoneOption option = rawOption.bake(boneName);
                    boneOptions.add(option);
                }
            }

            return new RawAnimation(loop, animationLength, !boneOptions.isEmpty() ? boneOptions : Empty.list());
        }
    }
}
