package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3f;
import ru.timeconqueror.timecore.animation.component.CatmullRomKeyFrame;
import ru.timeconqueror.timecore.animation.component.KeyFrame;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeyFrameListDeserializer implements JsonDeserializer<List<KeyFrame>> {
    @Override
    public List<KeyFrame> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        List<KeyFrame> keyFrames = new ArrayList<>();

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

//                    if(frame.has("pre")) {
//                        if(!first) {
//                            //FIXME https://docs.microsoft.com/en-us/minecraft/creator/reference/content/animationsreference/examples/animationgettingstarted#discontinuous-example
//                            throw new JsonSyntaxException("Can't handle the 'pre' property of the non-first keyframe");
//                        }
//
//                        Vector3f vec = memberAsVector(frame, "pre", ctx);
//                        keyFrames.add(new KeyFrame(0, vec));
//                    }

                    String lerpMode = JSONUtils.getAsString(frame, "lerp_mode");
                    Vector3f vec = memberAsVector(frame, "post", ctx);
                    if (lerpMode.equals("catmullrom")) {
                        keyFrames.add(catmullRomFrame(time, vec));
                    } else if (lerpMode.equals("linear")) {
                        keyFrames.add(frame(time, vec));
                    } else {
                        throw new JsonSyntaxException("Can't parse keyframe with location " + key + " (" + value + "), because it has unknown lerp_mode type: " + lerpMode);
                    }
                } else if (value.isJsonArray()) {
                    Vector3f vec = asVector(value, ctx);
                    keyFrames.add(frame(time, vec));
                } else {
                    throw new JsonSyntaxException("Can't parse keyframe with location " + key + " (" + value + "), because it has unknown type: " + JSONUtils.getType(value));
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
        return new KeyFrame((int) (time * 1000), vec);
    }

    private static KeyFrame catmullRomFrame(float time, Vector3f vec) {
        return new CatmullRomKeyFrame((int) (time * 1000), vec);
    }
}
