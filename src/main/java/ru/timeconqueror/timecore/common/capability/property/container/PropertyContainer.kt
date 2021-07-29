package ru.timeconqueror.timecore.common.capability.property.container

import net.minecraft.nbt.CompoundNBT
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import ru.timeconqueror.timecore.common.capability.property.serializer.*
import java.util.function.Predicate

open class PropertyContainer {
    private val properties = mutableListOf<CoffeeProperty<*>>()
    private val containers = mutableListOf<NamedPropertyContainer>()

    protected open fun <V> prop(name: String, value: V, serializer: IPropertySerializer<V>): CoffeeProperty<V> {
        val prop = CoffeeProperty(name, value, serializer)
        properties.add(prop)
        return prop
    }

    protected open fun prop(name: String, value: Int): CoffeeProperty<Int> {
        return prop(name, value, IntPropertySerializer)
    }

    protected open fun prop(name: String, value: Long): CoffeeProperty<Long> {
        return prop(name, value, LongPropertySerializer)
    }

    protected open fun prop(name: String, value: Float): CoffeeProperty<Float> {
        return prop(name, value, FloatPropertySerializer)
    }

    protected open fun prop(name: String, value: Double): CoffeeProperty<Double> {
        return prop(name, value, DoublePropertySerializer)
    }

    protected open fun prop(name: String, value: Boolean): CoffeeProperty<Boolean> {
        return prop(name, value, BooleanPropertySerializer)
    }

    protected open fun prop(name: String, value: String): CoffeeProperty<String> {
        return prop(name, value, StringPropertySerializer)
    }

    protected open fun nullableProp(name: String, value: Int?): CoffeeProperty<Int?> {
        return prop(name, value, IntPropertySerializer.Nullable)
    }

    protected open fun nullableProp(name: String, value: Long?): CoffeeProperty<Long?> {
        return prop(name, value, LongPropertySerializer.Nullable)
    }

    protected open fun nullableProp(name: String, value: Float?): CoffeeProperty<Float?>? {
        return prop(name, value, FloatPropertySerializer.Nullable)
    }

    protected open fun nullableProp(name: String, value: Double?): CoffeeProperty<Double?>? {
        return prop(name, value, DoublePropertySerializer.Nullable)
    }

    protected open fun nullableProp(name: String, value: Boolean?): CoffeeProperty<Boolean?>? {
        return prop(name, value, BooleanPropertySerializer.Nullable)
    }

    protected open fun nullableProp(name: String, value: String?): CoffeeProperty<String?>? {
        return prop(name, value, StringPropertySerializer.Nullable)
    }

    protected open fun <T : NamedPropertyContainer> container(name: String, value: T): T {
        containers.add(value)
        return value
    }

    fun serialize(
        serializePredicate: Predicate<CoffeeProperty<*>>,
        nbt: CompoundNBT,
        clientSide: Boolean
    ): Boolean {
        var hasChanges = false
        for (property in properties) {
            if (property.isClientDependent() == clientSide && serializePredicate.test(property)) {
                property.serialize(nbt)
                hasChanges = true
            }
        }
        for (container in containers) {
            val containerNBT = CompoundNBT()
            if (container.serialize(serializePredicate, containerNBT, clientSide)) {
                nbt.put(container.name, containerNBT)
                hasChanges = true
            }
        }
        return hasChanges
    }

    fun deserialize(nbt: CompoundNBT) {
        for (property in properties) {
            property.deserialize(nbt)
        }
        for (container in containers) {
            if (nbt.contains(container.name)) {
                container.deserialize(nbt)
            }
        }
    }

    fun deserialize(nbt: CompoundNBT, sentFromClient: Boolean) {
        for (property in properties) {
            property.deserialize(nbt, sentFromClient)
        }
        for (container in containers) {
            if (nbt.contains(container.name)) {
                container.deserialize(nbt, sentFromClient)
            }
        }
    }
}