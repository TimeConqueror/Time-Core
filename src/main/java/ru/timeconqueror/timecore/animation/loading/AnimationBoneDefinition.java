package ru.timeconqueror.timecore.animation.loading;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.UnmodifiableView;
import ru.timeconqueror.timecore.animation.component.AnimationBone;
import ru.timeconqueror.timecore.animation.component.IKeyFrame;
import ru.timeconqueror.timecore.animation.util.Empty;
import ru.timeconqueror.timecore.api.animation.Channel;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class AnimationBoneDefinition {
    private final List<IKeyFrame> rotations;
    private final List<IKeyFrame> positions;
    private final List<IKeyFrame> scales;

    public AnimationBone bake(String name) {
        return new AnimationBone(name, rotations, positions, scales);
    }

    public static class Deserializer implements JsonDeserializer<AnimationBoneDefinition> {
        @Override
        public AnimationBoneDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject optionJson = json.getAsJsonObject();

            List<IKeyFrame> rotationFrames = parseKeyFrameArr(optionJson, "rotation", Channel.ROTATION, ctx);

            List<IKeyFrame> positionFrames = parseKeyFrameArr(optionJson, "position", Channel.TRANSLATION, ctx);

            List<IKeyFrame> scaleFrames = parseKeyFrameArr(optionJson, "scale", Channel.SCALE, ctx);

            return new AnimationBoneDefinition(rotationFrames, positionFrames, scaleFrames);
        }

        @UnmodifiableView
        private List<IKeyFrame> parseKeyFrameArr(JsonObject json, String optionName, Channel channel, JsonDeserializationContext ctx) {
            if (json.has(optionName)) {
                List<IKeyFrame> keyFrames = KeyFrameListDeserializer.DESERIALIZERS.get(channel)
                        .deserialize(json.get(optionName), List.class, ctx);
                return Collections.unmodifiableList(keyFrames);
            }

            return Empty.list();
        }
    }
}
