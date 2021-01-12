package ru.timeconqueror.timecore.api.client.resource;

import com.google.common.base.Joiner;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class JSONTimeResource implements TimeResource {
    public static String object(@Nullable String key, String... children) {
        StringBuilder str = new StringBuilder();
        if (key != null) {
            str.append("\"").append(key).append("\":");
        }

        str.append("{").append(listOf(children)).append("}");

        return str.toString();
    }

    public static String array(@Nullable String key, String... children) {
        StringBuilder str = new StringBuilder();
        if (key != null) {
            str.append("\"").append(key).append("\":");
        }

        str.append("[").append(listOf(children)).append("]");

        return str.toString();
    }

    public static String property(String key, String value) {
        return "\"" + key + "\":" + "\"" + value + "\"";
    }

    public static String property(String key, int value) {
        return "\"" + key + "\":" + value;
    }

    public static String property(String key, float value) {
        return "\"" + key + "\":" + value;
    }

    public static String listOf(String... objects) {
        return Joiner.on(',').join(objects);
    }

    public static String listOf(Supplier<String[]> objectSupplier) {
        return listOf(objectSupplier.get());
    }

    public static String listOf(ArrayList<String> objects) {
        return Joiner.on(',').join(objects);
    }

    @Override
    public byte[] toBytes() {
        return toJson().getBytes(StandardCharsets.UTF_8);
    }

    public abstract String toJson();

    @Override
    public String toString() {
        return toJson();
    }
}
