package ru.timeconqueror.timecore.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty;
import ru.timeconqueror.timecore.common.capability.property.serializer.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
public abstract class CoffeeCapability<T extends ICapabilityProvider> implements INBTSerializable<CompoundNBT>, ICoffeeCapability<T> {
    private final ArrayList<CoffeeProperty<?>> properties = new ArrayList<>();

    @NotNull
    @Override
    public ArrayList<CoffeeProperty<?>> getProperties() {
        return properties;
    }

    protected <V> CoffeeProperty<V> prop(String name, @Nullable V value, IPropertySerializer<V> serializer) {
        CoffeeProperty<V> prop = new CoffeeProperty<>(name, value, serializer);
        properties.add(prop);
        return prop;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        serializeProperties(prop -> true, nbt, false);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        deserializeProperties(nbt);
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

    protected CoffeeProperty<Integer> nullableProp(String name, @Nullable Integer value) {
        return prop(name, value, IntPropertySerializer.Nullable.INSTANCE);
    }

    protected CoffeeProperty<Long> nullableProp(String name, @Nullable Long value) {
        return prop(name, value, LongPropertySerializer.Nullable.INSTANCE);
    }

    protected CoffeeProperty<Float> nullableProp(String name, @Nullable Float value) {
        return prop(name, value, FloatPropertySerializer.Nullable.INSTANCE);
    }

    protected CoffeeProperty<Double> nullableProp(String name, @Nullable Double value) {
        return prop(name, value, DoublePropertySerializer.Nullable.INSTANCE);
    }

    protected CoffeeProperty<Boolean> nullableProp(String name, @Nullable Boolean value) {
        return prop(name, value, BooleanPropertySerializer.Nullable.INSTANCE);
    }

    protected CoffeeProperty<String> nullableProp(String name, @Nullable String value) {
        return prop(name, value, StringPropertySerializer.Nullable.INSTANCE);
    }
}
