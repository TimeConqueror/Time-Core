package ru.timeconqueror.timecore.api.util.json;

import com.google.gson.JsonElement;
import com.google.gson.*;
import net.minecraft.world.phys.Vec2;

import java.lang.reflect.Type;

public class Vec2JsonAdapter implements JsonSerializer<Vec2>, JsonDeserializer<Vec2> {
    @Override
    public Vec2 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        if (array.size() != 2) {
            throw new JsonSyntaxException("Expected 2 elements in vec2 array, found: " + array.size());
        }

        return new Vec2(array.get(0).getAsFloat(), array.get(1).getAsFloat());
    }

    @Override
    public JsonElement serialize(Vec2 src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        array.add(src.x);
        array.add(src.y);
        return array;
    }
}
