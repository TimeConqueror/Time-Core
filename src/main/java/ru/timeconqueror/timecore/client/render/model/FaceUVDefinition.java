package ru.timeconqueror.timecore.client.render.model;

import com.google.gson.*;
import ru.timeconqueror.timecore.api.util.Vec2i;

import java.lang.reflect.Type;

public record FaceUVDefinition(Vec2i uv, Vec2i size) {
    public static class Deserializer implements JsonDeserializer<FaceUVDefinition> {
        @Override
        public FaceUVDefinition deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = jsonIn.getAsJsonObject();

            Vec2i uv = context.deserialize(json.get("uv"), Vec2i.class);
            Vec2i size = context.deserialize(json.get("uv_size"), Vec2i.class);

            return new FaceUVDefinition(uv, size);
        }
    }
}
