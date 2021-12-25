package ru.timeconqueror.timecore.api.util;

import com.google.gson.JsonElement;
import com.google.gson.*;
import com.mojang.math.Vector3f;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec2;

public class JsonUtils {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();

    public static Vector3f getAsVec3f(JsonObject parent, String memberName) {
        return toVec3f(GsonHelper.getAsJsonArray(parent, memberName), memberName);
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

        return new Vector3f(GsonHelper.convertToFloat(array.get(0), "member of '" + name + "'"),
                GsonHelper.convertToFloat(array.get(1), "member of '" + name + "'"),
                GsonHelper.convertToFloat(array.get(2), "member of '" + name + "'"));
    }

    public static Vec2 getAsVec2f(JsonObject parent, String memberName) {
        return toVec2f(GsonHelper.getAsJsonArray(parent, memberName), memberName);
    }

    public static Vec2 getAsVec3f(JsonObject parent, String memberName, Vec2 defaultVal) {
        if (parent.has(memberName)) {
            return getAsVec2f(parent, memberName);
        } else {
            return defaultVal;
        }
    }

    public static Vec2 toVec2f(JsonArray array, String name) {
        if (array.size() != 2) {
            throw new JsonSyntaxException("Expected 2 elements in '" + name + "' array, found: " + array.size());
        }

        return new Vec2(GsonHelper.convertToFloat(array.get(0), "member of '" + name + "'"),
                GsonHelper.convertToFloat(array.get(1), "member of '" + name + "'"));
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
