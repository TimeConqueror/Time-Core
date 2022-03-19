package ru.timeconqueror.timecore.api.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public class Vec2i {
    private final int x;
    private final int y;

    public Vec2i() {
        this(0, 0);
    }

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public static class JsonAdapter implements JsonSerializer<Vec2i>, JsonDeserializer<Vec2i> {
        @Override
        public Vec2i deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            if (array.size() != 2) {
                throw new JsonSyntaxException("Expected 2 elements in vec2i array, found: " + array.size());
            }

            return new Vec2i(array.get(0).getAsInt(), array.get(1).getAsInt());
        }

        @Override
        public JsonElement serialize(Vec2i src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray();
            array.add(src.x);
            array.add(src.y);
            return array;
        }
    }
}
