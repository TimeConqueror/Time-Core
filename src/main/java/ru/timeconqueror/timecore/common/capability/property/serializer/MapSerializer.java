package ru.timeconqueror.timecore.common.capability.property.serializer;

import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MapSerializer<K, V> extends IPropertySerializer<Map<K, V>> {
    static <K, V> StringBasedKeyMapSerializer<K, V> withStringBasedKey(Supplier<Map<K, V>> mapCreator,
                                                                       Function<K, String> keySerializer,
                                                                       Function<String, K> keyDeserializer,
                                                                       IPropertySerializer<V> valueSerializer) {
        return new StringBasedKeyMapSerializer<>(mapCreator, keySerializer, keyDeserializer, valueSerializer);
    }

    class StringBasedKeyMapSerializer<K, V> implements MapSerializer<K, V> {
        static final Logger log = LogManager.getLogger();

        private final Supplier<Map<K, V>> mapCreator;
        private final IPropertySerializer<V> valueSerializer;

        private final Function<K, String> keySerializer;
        private final Function<String, K> keyDeserializer;

        public StringBasedKeyMapSerializer(Supplier<Map<K, V>> mapCreator,
                                           Function<K, String> keySerializer,
                                           Function<String, K> keyDeserializer,
                                           IPropertySerializer<V> valueSerializer) {
            this.mapCreator = mapCreator;
            this.valueSerializer = valueSerializer;
            this.keySerializer = keySerializer;
            this.keyDeserializer = keyDeserializer;
        }

        @Override
        public void serialize(@NotNull String name, Map<K, V> map, @NotNull CompoundTag nbt) {
            CompoundTag tag = new CompoundTag();

            int times = 0;
            for (Map.Entry<K, V> e : map.entrySet()) {
                String key = keySerializer.apply(e.getKey());
                if (tag.contains(key)) {
                    times++;
                    log.warn("Serializing same key twice: {} -> {}", e, key);

                    if (times % 5 == 0) {
                        new IllegalArgumentException().printStackTrace();
                    }
                }

                valueSerializer.serialize(key, e.getValue(), tag);
            }

            nbt.put(name, tag);
        }

        @Override
        public Map<K, V> deserialize(@NotNull String name, @NotNull CompoundTag nbt) {
            CompoundTag tag = nbt.getCompound(name);
            Map<K, V> map = mapCreator.get();

            int times = 0;
            for (String key : tag.getAllKeys()) {
                K deserializedKey = keyDeserializer.apply(key);
                V deserializedVal = valueSerializer.deserialize(key, tag);
                if (map.put(deserializedKey, deserializedVal) != null) {
                    log.warn("Deserializing same key twice: {} -> {}", key, deserializedKey);

                    times++;
                    if (times % 5 == 0) {
                        new IllegalArgumentException().printStackTrace();
                    }
                }
            }

            return map;
        }
    }
}
