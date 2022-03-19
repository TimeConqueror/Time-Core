package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.*;
import com.mojang.math.Vector3f;
import ru.timeconqueror.timecore.animation.component.BoneOption;
import ru.timeconqueror.timecore.animation.component.KeyFrame;
import ru.timeconqueror.timecore.animation.util.Empty;
import ru.timeconqueror.timecore.api.util.MathUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RawBoneOption {
    private final List<KeyFrame> rotations;
    private final List<KeyFrame> positions;
    private final List<KeyFrame> scales;

    public RawBoneOption(List<KeyFrame> rotations, List<KeyFrame> positions, List<KeyFrame> scales) {
        this.rotations = rotations;
        this.positions = positions;
        this.scales = scales;
    }

    public BoneOption bake(String name) {
        return new BoneOption(name, rotations, positions, scales);
    }

    public static class Deserializer implements JsonDeserializer<RawBoneOption> {
        @Override
        public RawBoneOption deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject optionJson = json.getAsJsonObject();

            List<KeyFrame> rotationFrames = parseKeyFrameArr(optionJson, "rotation", ctx, vec -> {
                vec.set(MathUtils.toRadians(vec.x()), MathUtils.toRadians(vec.y()), MathUtils.toRadians(vec.z()));
                vec.mul(-1, -1, 1);
            });
            List<KeyFrame> positionFrames = parseKeyFrameArr(optionJson, "position", ctx, Empty.consumer());

            List<KeyFrame> scaleFrames = parseKeyFrameArr(optionJson, "scale", ctx, Empty.consumer());

            return new RawBoneOption(rotationFrames, positionFrames, scaleFrames);
        }

        private List<KeyFrame> parseKeyFrameArr(JsonObject json, String optionName, JsonDeserializationContext ctx, Consumer<Vector3f> vecProcessor) {
            if (json.has(optionName)) {
                List<KeyFrame> keyFrames = ctx.deserialize(json.get(optionName), KeyFrameListDeserializer.KEYFRAME_LIST_TYPE);
                for (KeyFrame frame : keyFrames) {
                    vecProcessor.accept(frame.getVec());
                }

                return Collections.unmodifiableList(keyFrames);
            }

            return Empty.list();
        }
    }
}
