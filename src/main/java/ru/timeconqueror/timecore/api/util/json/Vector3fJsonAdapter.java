package ru.timeconqueror.timecore.api.util.json;

import com.google.gson.JsonElement;
import com.google.gson.*;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Vector3fJsonAdapter implements JsonSerializer<Vector3f>, JsonDeserializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray() && !json.isJsonPrimitive()) {
            throw new JsonSyntaxException("Expected to read an array or a primitive");
        }

        if (json.isJsonPrimitive()) {
            return new Vector3f(json.getAsFloat(), json.getAsFloat(), json.getAsFloat());
        }

        JsonArray array = json.getAsJsonArray();
        if (array.size() != 3 && array.size() != 1) {
            throw new JsonSyntaxException("Expected 1 or 3 elements in vec3f array, found: " + array.size());
        }

        if (array.size() == 1) {
            return new Vector3f(array.get(0).getAsFloat(), array.get(0).getAsFloat(), array.get(0).getAsFloat());
        } else {
            return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
        }
    }

    @Override
    public JsonElement serialize(Vector3f src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        array.add(src.x());
        array.add(src.y());
        array.add(src.z());
        return array;
    }
}
