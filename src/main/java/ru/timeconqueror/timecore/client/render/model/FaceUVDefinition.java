package ru.timeconqueror.timecore.client.render.model;

import com.google.gson.*;
import ru.timeconqueror.timecore.api.util.Vec2i;

import java.lang.reflect.Type;

public class FaceUVDefinition {
    private final Vec2i uv;
    private final Vec2i size;

    public FaceUVDefinition(Vec2i uv, Vec2i size) {
        this.uv = uv;
        this.size = size;
    }

    public Vec2i uv() {
        return uv;
    }

    public Vec2i size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaceUVDefinition)) return false;

        FaceUVDefinition that = (FaceUVDefinition) o;

        if (!uv.equals(that.uv)) return false;
        return size.equals(that.size);
    }

    @Override
    public int hashCode() {
        int result = uv.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }

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
