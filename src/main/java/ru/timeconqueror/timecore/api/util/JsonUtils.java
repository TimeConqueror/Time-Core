package ru.timeconqueror.timecore.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.client.renderer.Vector3f;

import javax.vecmath.Vector2f;

public class JsonUtils {
    public static Vector3f getVec3f(String name, JsonElement parent) {
        JsonArray arr = parent.getAsJsonObject().get(name).getAsJsonArray();
        return new Vector3f(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat());
    }

    public static Vector3f getVec3f(String name, JsonElement parent, Vector3f defaultVal) {
        JsonElement vecArr = parent.getAsJsonObject().get(name);
        if (vecArr != null) {
            JsonArray arr = vecArr.getAsJsonArray();
            return new Vector3f(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat());
        } else return defaultVal;
    }

    public static Vector2f getVec2f(String name, JsonElement parent) {
        JsonArray arr = parent.getAsJsonObject().get(name).getAsJsonArray();
        return new Vector2f(arr.get(0).getAsFloat(), arr.get(1).getAsFloat());
    }

    public static boolean getBoolean(String name, JsonElement parent) {
        return parent.getAsJsonObject().get(name).getAsBoolean();
    }

    public static boolean getBoolean(String name, JsonElement parent, boolean defaultVal) {
        JsonElement val = parent.getAsJsonObject().get(name);
        return val != null ? val.getAsBoolean() : defaultVal;
    }

    public static float getFloat(String name, JsonElement parent) {
        return parent.getAsJsonObject().get(name).getAsFloat();
    }

    public static float getFloat(String name, JsonElement parent, float defaultVal) {
        JsonElement val = parent.getAsJsonObject().get(name);
        return val != null ? val.getAsFloat() : defaultVal;
    }

    public static int getInt(String name, JsonElement parent) {
        return parent.getAsJsonObject().get(name).getAsInt();
    }

    public static int getInt(String name, JsonElement parent, int defaultVal) {
        JsonElement val = parent.getAsJsonObject().get(name);
        return val != null ? val.getAsInt() : defaultVal;
    }

    public static String getString(String name, JsonElement parent) {
        return parent.getAsJsonObject().get(name).getAsString();
    }

    public static String getString(String name, JsonElement parent, String defaultVal) {
        JsonElement val = parent.getAsJsonObject().get(name);
        return val != null ? val.getAsString() : defaultVal;
    }
}
