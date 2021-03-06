package ru.timeconqueror.timecore.api.util;

import com.google.gson.JsonElement;
import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;

public class JsonUtils {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();

    public static Vector3f getAsVec3f(JsonObject parent, String memberName) {
        return toVec3f(JSONUtils.getAsJsonArray(parent, memberName), memberName);
    }

    public static Vector3f getAsVec3f(JsonObject parent, String memberName, Vector3f defaultVal) {
        if (parent.has(memberName)) {
            return getAsVec3f(parent, memberName);
        } else {
            return defaultVal;
        }
    }

    public static Vector3f toVec3f(JsonArray array, String name) {
        if (array.size() != 3) {
            throw new JsonSyntaxException("Expected 3 elements in '" + name + "' array, found: " + array.size());
        }

        return new Vector3f(JSONUtils.convertToFloat(array.get(0), "member of '" + name + "'"),
                JSONUtils.convertToFloat(array.get(1), "member of '" + name + "'"),
                JSONUtils.convertToFloat(array.get(2), "member of '" + name + "'"));
    }

    public static Vector2f getAsVec2f(JsonObject parent, String memberName) {
        return toVec2f(JSONUtils.getAsJsonArray(parent, memberName), memberName);
    }

    public static Vector2f getAsVec3f(JsonObject parent, String memberName, Vector2f defaultVal) {
        if (parent.has(memberName)) {
            return getAsVec2f(parent, memberName);
        } else {
            return defaultVal;
        }
    }

    public static Vector2f toVec2f(JsonArray array, String name) {
        if (array.size() != 2) {
            throw new JsonSyntaxException("Expected 2 elements in '" + name + "' array, found: " + array.size());
        }

        return new Vector2f(JSONUtils.convertToFloat(array.get(0), "member of '" + name + "'"),
                JSONUtils.convertToFloat(array.get(1), "member of '" + name + "'"));
    }

    public static JsonElement get(JsonObject parent, String memberName, Class<? extends JsonElement> expectedType) {
        verifyExisting(parent, memberName, expectedType);
        return parent.get(memberName);
    }

    private static void verifyExisting(JsonObject parent, String memberName, Class<? extends JsonElement> expectedType) {
        if (!parent.has(memberName))
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a " + expectedType.getSimpleName());
    }
}
