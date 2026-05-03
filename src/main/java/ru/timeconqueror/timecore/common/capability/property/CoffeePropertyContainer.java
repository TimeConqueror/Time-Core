package ru.timeconqueror.timecore.common.capability.property;

import net.minecraft.nbt.CompoundTag;
import ru.timeconqueror.timecore.api.common.blockentity.SerializationType;
import ru.timeconqueror.timecore.common.capability.property.serializer.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CoffeePropertyContainer {
    private final List<CoffeeProperty<?>> properties = new ArrayList<>();
    private final Map<String, CoffeePropertyContainer> containers = new HashMap<>();

    protected <V> CoffeeProperty<V> prop(String name, V value, IPropertySerializer<V> serializer) {
        CoffeeProperty<V> prop = new CoffeeProperty<>(name, value, serializer);
        properties.add(prop);
        return prop;
    }

    protected CoffeeProperty<Integer> prop(String name, int value) {
        return prop(name, value, IntPropertySerializer.INSTANCE);
    }

    protected CoffeeProperty<Long> prop(String name, long value) {
        return prop(name, value, LongPropertySerializer.INSTANCE);
    }

    protected CoffeeProperty<Float> prop(String name, float value) {
        return prop(name, value, FloatPropertySerializer.INSTANCE);
    }

    protected CoffeeProperty<Double> prop(String name, double value) {
        return prop(name, value, DoublePropertySerializer.INSTANCE);
    }

    protected CoffeeProperty<Boolean> prop(String name, boolean value) {
        return prop(name, value, BooleanPropertySerializer.INSTANCE);
    }

    protected CoffeeProperty<String> prop(String name, String value) {
        return prop(name, value, StringPropertySerializer.INSTANCE);
    }

    protected <T> CoffeeProperty<CoffeeObservableList<T>> prop(String name, Supplier<List<T>> listCreator,
                                                               IPropertySerializer<T> entrySerializer) {
        return prop(name, CoffeeObservableList.observe(listCreator.get()),
                new ObservableListSerializer<>(listCreator, entrySerializer));
    }

    protected CoffeeProperty<Integer> nullableProp(String name, Integer value) {
        return prop(name, value, IntPropertySerializer.NULLABLE);
    }

    protected CoffeeProperty<Long> nullableProp(String name, Long value) {
        return prop(name, value, LongPropertySerializer.NULLABLE);
    }

    protected CoffeeProperty<Float> nullableProp(String name, Float value) {
        return prop(name, value, FloatPropertySerializer.NULLABLE);
    }

    protected CoffeeProperty<Double> nullableProp(String name, Double value) {
        return prop(name, value, DoublePropertySerializer.NULLABLE);
    }

    protected CoffeeProperty<Boolean> nullableProp(String name, Boolean value) {
        return prop(name, value, BooleanPropertySerializer.NULLABLE);
    }

    protected CoffeeProperty<String> nullableProp(String name, String value) {
        return prop(name, value, StringPropertySerializer.NULLABLE);
    }

    protected <T extends CoffeePropertyContainer> T container(String name, T value) {
        if (containers.put(name, value) != null) {
            throw new IllegalArgumentException("The container with name '" + name + "' has been already registered!");
        }
        return value;
    }

    public boolean serialize(Predicate<CoffeeProperty<?>> serializePredicate,
                             CompoundTag nbt,
                             boolean clientSide,
                             SerializationType type) {
        boolean hasChanges = false;
        for (CoffeeProperty<?> property : properties) {
            if (property.isClientDependent() == clientSide && serializePredicate.test(property)) {
                property.serialize(nbt, type);
                hasChanges = true;
            }
        }
        for (Map.Entry<String, CoffeePropertyContainer> entry : containers.entrySet()) {
            String name = entry.getKey();
            CoffeePropertyContainer container = entry.getValue();
            CompoundTag containerNBT = new CompoundTag();
            if (container.serialize(serializePredicate, containerNBT, clientSide, type)) {
                nbt.put(name, containerNBT);
                hasChanges = true;
            }
        }
        return hasChanges;
    }

    public void deserialize(CompoundTag nbt) {
        for (CoffeeProperty<?> property : properties) {
            property.deserialize(nbt);
        }
        for (Map.Entry<String, CoffeePropertyContainer> entry : containers.entrySet()) {
            String name = entry.getKey();
            CoffeePropertyContainer container = entry.getValue();
            if (nbt.contains(name)) {
                container.deserialize(nbt.getCompound(name));
            }
        }
    }

    public void deserialize(CompoundTag nbt, boolean sentFromClient) {
        for (CoffeeProperty<?> property : properties) {
            property.deserialize(nbt, sentFromClient);
        }
        for (Map.Entry<String, CoffeePropertyContainer> entry : containers.entrySet()) {
            String name = entry.getKey();
            CoffeePropertyContainer container = entry.getValue();
            if (nbt.contains(name)) {
                container.deserialize(nbt.getCompound(name), sentFromClient);
            }
        }
    }
}
