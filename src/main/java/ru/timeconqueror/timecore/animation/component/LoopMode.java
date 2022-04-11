package ru.timeconqueror.timecore.animation.component;

import com.google.gson.*;

import java.lang.reflect.Type;

public enum LoopMode {
    DO_NOT_LOOP,
    HOLD_ON_LAST_FRAME,
    LOOP;

    public static class Deserializer implements JsonDeserializer<LoopMode> {

        @Override
        public LoopMode deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();

            String loopMode = primitive.getAsString();
            switch (loopMode) {
                case "false":
                    return LoopMode.DO_NOT_LOOP;
                case "true":
                    return LoopMode.LOOP;
                case "hold_on_last_frame":
                    return LoopMode.HOLD_ON_LAST_FRAME;
                default: {
                    throw new JsonSyntaxException("Unknown loop mode type: " + loopMode);
                }
            }
        }
    }
}
