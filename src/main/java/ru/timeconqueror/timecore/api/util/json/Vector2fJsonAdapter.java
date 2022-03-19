package ru.timeconqueror.timecore.api.util.json;

import com.google.gson.JsonElement;
import com.google.gson.*;
import net.minecraft.util.math.vector.Vector2f;

import java.lang.reflect.Type;

public class Vector2fJsonAdapter implements JsonSerializer<Vector2f>, JsonDeserializer<Vector2f> {
    @Override
    public Vector2f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        if (array.size() != 2) {
            throw new JsonSyntaxException("Expected 2 elements in vec2 array, found: " + array.size());
        }

        return new Vector2f(array.get(0).getAsFloat(), array.get(1).getAsFloat());
    }

    @Override
    public JsonElement serialize(Vector2f src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        array.add(src.x);
        array.add(src.y);
        return array;
    }
}
