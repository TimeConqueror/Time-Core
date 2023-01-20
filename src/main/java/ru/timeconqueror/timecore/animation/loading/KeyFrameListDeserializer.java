package ru.timeconqueror.timecore.animation.loading;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.animation.component.CatmullRomKeyFrame;
import ru.timeconqueror.timecore.animation.component.IKeyFrame;
import ru.timeconqueror.timecore.animation.component.KeyFrame;
import ru.timeconqueror.timecore.animation.component.StepKeyFrame;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeyFrameListDeserializer implements JsonDeserializer<List<IKeyFrame>> {
    public static final Type KEYFRAME_LIST_TYPE = new TypeToken<List<IKeyFrame>>() {
    }.getType();

    @Override
    public List<IKeyFrame> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        List<IKeyFrame> keyFrames = new ArrayList<>();

        if (json.isJsonArray() || json.isJsonPrimitive()) {
            Vector3f vec = asVector(json, ctx);
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
                        Vector3f pre = memberAsVector(frame, "pre", ctx);
                        Vector3f post = memberAsVector(frame, "post", ctx);
                        keyFrames.add(new StepKeyFrame(toMillis(time), pre, post));
                        continue;
                    }

                    String lerpMode = GsonHelper.getAsString(frame, "lerp_mode");
                    Vector3f vec = memberAsVector(frame, "post", ctx);
                    if (lerpMode.equals("catmullrom")) {
                        keyFrames.add(new CatmullRomKeyFrame(toMillis(time), vec));
                    } else if (lerpMode.equals("linear")) {
                        keyFrames.add(frame(time, vec));
                    } else {
                        throw new JsonSyntaxException("Can't parse keyframe with location " + key + " (" + value + "), because it has unknown lerp_mode type: " + lerpMode);
                    }
                } else if (value.isJsonArray()) {
                    Vector3f vec = asVector(value, ctx);
                    keyFrames.add(frame(time, vec));
                } else {
                    throw new JsonSyntaxException("Can't parse keyframe with location " + key + " (" + value + "), because it has unknown type: " + GsonHelper.getType(value));
                }
            }
        }

        return !keyFrames.isEmpty() ? Collections.unmodifiableList(keyFrames) : Collections.emptyList();
    }

    private Vector3f memberAsVector(JsonObject parent, String memberName, JsonDeserializationContext ctx) {
        return asVector(parent.get(memberName), ctx);
    }

    private Vector3f asVector(JsonElement json, JsonDeserializationContext ctx) {
        return ctx.deserialize(json, Vector3f.class);
    }

    private static KeyFrame frame(float time, Vector3f vec) {
        return new KeyFrame(toMillis(time), vec);
    }

    private static int toMillis(float seconds) {
        return (int) (seconds * 1000);
    }
}
