package ru.timeconqueror.timecore.client.render.model;

import com.google.gson.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import ru.timeconqueror.timecore.api.util.Vec2i;
import ru.timeconqueror.timecore.client.render.model.uv.BoxUVResolver;
import ru.timeconqueror.timecore.client.render.model.uv.FacedUVResolver;
import ru.timeconqueror.timecore.client.render.model.uv.UVResolver;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.EnumMap;

public interface UVDefinition {
    UVResolver bake(Vector3f size);

    class Deserializer implements JsonDeserializer<UVDefinition> {
        private static final Simple.Deserializer simpleDecoder = new Simple.Deserializer();
        private static final PerFace.Deserializer perFaceDecoder = new PerFace.Deserializer();

        @Override
        public UVDefinition deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonIn.isJsonArray()) {
                return simpleDecoder.deserialize(jsonIn, typeOfT, context);
            } else {
                return perFaceDecoder.deserialize(jsonIn, typeOfT, context);
            }
        }
    }

    /**
     * uv - The starting point in the texture foo.png (x -> horizontal, y -> vertical) for that cube.
     */
    final class Simple implements UVDefinition {
        private final Vec2i uv;

        private Simple(Vec2i uv) {
            this.uv = uv;
        }

        @Override
        public UVResolver bake(Vector3f size) {
            return new BoxUVResolver(uv, size);
        }

        private static class Deserializer implements JsonDeserializer<Simple> {
            @Override
            public Simple deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Simple(context.deserialize(jsonIn, Vec2i.class));
            }
        }
    }

    final class PerFace implements UVDefinition {
        private final EnumMap<Direction, FaceUVDefinition> mappings;

        private PerFace(EnumMap<Direction, FaceUVDefinition> mappings) {
            this.mappings = mappings;
        }

        @Override
        public UVResolver bake(Vector3f size) {
            return new FacedUVResolver(Collections.unmodifiableMap(mappings));
        }

        private static class Deserializer implements JsonDeserializer<PerFace> {
            public static final Direction[] DIRECTIONS = Direction.values();

            @Override
            public PerFace deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject json = jsonIn.getAsJsonObject();
                EnumMap<Direction, FaceUVDefinition> map = new EnumMap<>(Direction.class);

                for (Direction direction : DIRECTIONS) {
                    FaceUVDefinition face = context.deserialize(json.get(direction.getName()), FaceUVDefinition.class);

                    if (face == null) {
                        face = new FaceUVDefinition(new Vec2i(), new Vec2i());
                    }

                    map.put(direction, face);

                }
                return new PerFace(map);
            }
        }
    }
}
