package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.*;
import com.mojang.math.Vector3f;
import org.jetbrains.annotations.UnmodifiableView;
import ru.timeconqueror.timecore.animation.component.BoneOption;
import ru.timeconqueror.timecore.animation.component.IKeyFrame;
import ru.timeconqueror.timecore.animation.component.KeyFrameState;
import ru.timeconqueror.timecore.animation.util.Empty;
import ru.timeconqueror.timecore.api.util.MathUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RawBoneOption {
    private final List<IKeyFrame> rotations;
    private final List<IKeyFrame> positions;
    private final List<IKeyFrame> scales;

    public RawBoneOption(List<IKeyFrame> rotations, List<IKeyFrame> positions, List<IKeyFrame> scales) {
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

            List<IKeyFrame> rotationFrames = parseKeyFrameArr(optionJson, "rotation", ctx, vec -> {
                vec.set(MathUtils.toRadians(vec.x()), MathUtils.toRadians(vec.y()), MathUtils.toRadians(vec.z()));
                vec.mul(-1, -1, 1);
            });

            List<IKeyFrame> positionFrames = parseKeyFrameArr(optionJson, "position", ctx, vector3f -> {
                vector3f.mul(-1, 1, 1);
            });

            List<IKeyFrame> scaleFrames = parseKeyFrameArr(optionJson, "scale", ctx, Empty.consumer());

            return new RawBoneOption(rotationFrames, positionFrames, scaleFrames);
        }

        @UnmodifiableView
        private List<IKeyFrame> parseKeyFrameArr(JsonObject json, String optionName, JsonDeserializationContext ctx, Consumer<Vector3f> vecProcessor) {
            if (json.has(optionName)) {
                List<IKeyFrame> keyFrames = ctx.deserialize(json.get(optionName), KeyFrameListDeserializer.KEYFRAME_LIST_TYPE);
                for (IKeyFrame frame : keyFrames) {
                    Vector3f vec = frame.getVec(KeyFrameState.PREV);
                    vecProcessor.accept(vec);
                    Vector3f next = frame.getVec(KeyFrameState.NEXT);
                    if (next != vec) {
                        vecProcessor.accept(next);
                    }
                }

                return Collections.unmodifiableList(keyFrames);
            }

            return Empty.list();
        }
    }
}
