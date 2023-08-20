package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import net.minecraft.util.GsonHelper;
import ru.timeconqueror.timecore.animation.component.Vector;
import ru.timeconqueror.timecore.animation.component.*;
import ru.timeconqueror.timecore.api.animation.Channel;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public class KeyFrameListDeserializer implements JsonDeserializer<List<IKeyFrame>> {

    public static final Map<Channel, KeyFrameListDeserializer> DESERIALIZERS = Arrays.stream(Channel.values())
            .collect(Collectors.toUnmodifiableMap(Function.identity(), KeyFrameListDeserializer::new));

    private final Channel channel;

    @Override
    public List<IKeyFrame> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        List<IKeyFrame> keyFrames = new ArrayList<>();

        if (json.isJsonArray() || json.isJsonPrimitive()) {
            Vector vec = asVector(json, ctx);
            keyFrames.add(new KeyFrame(0, vec));
        } else if (json.isJsonObject()) {
            float prevTime = -1;

            for (Map.Entry<String, JsonElement> keyEntry : json.getAsJsonObject().entrySet()) {
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

                    //https://docs.microsoft.com/en-us/minecraft/creator/reference/content/animationsreference/examples/animationgettingstarted#discontinuous-example
                    if (frame.has("pre")) {
                        Vector pre = memberAsVector(frame, "pre", ctx);
                        Vector post = memberAsVector(frame, "post", ctx);
                        keyFrames.add(new StepKeyFrame(toMillis(time), pre, post));
                        continue;
                    }

                    String lerpMode = GsonHelper.getAsString(frame, "lerp_mode");
                    Vector vec = memberAsVector(frame, "post", ctx);
                    if (lerpMode.equals("catmullrom")) {
                        keyFrames.add(new CatmullRomKeyFrame(toMillis(time), vec));
                    } else if (lerpMode.equals("linear")) {
                        keyFrames.add(new KeyFrame(toMillis(time), vec));
                    } else {
                        throw new JsonSyntaxException("Can't parse keyframe with location " + key + " (" + value + "), because it has unknown lerp_mode type: " + lerpMode);
                    }
                } else if (value.isJsonArray()) {
                    Vector vec = asVector(value, ctx);
                    keyFrames.add(new KeyFrame(toMillis(time), vec));
                } else {
                    throw new JsonSyntaxException("Can't parse keyframe with location " + key + " (" + value + "), because it has unknown type: " + GsonHelper.getType(value));
                }
            }
        }

        return !keyFrames.isEmpty() ? Collections.unmodifiableList(keyFrames) : Collections.emptyList();
    }

    private Vector memberAsVector(JsonObject parent, String memberName, JsonDeserializationContext ctx) {
        return asVector(parent.get(memberName), ctx);
    }

    private Vector asVector(JsonElement json, JsonDeserializationContext ctx) {
        return ctx.<VectorDefinition>deserialize(json, VectorDefinition.class)
                .build(this.channel);
    }

    private static int toMillis(float seconds) {
        return (int) (seconds * 1000);
    }
}
