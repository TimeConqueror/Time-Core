package ru.timeconqueror.timecore.common.capability.property

import net.minecraft.nbt.CompoundNBT
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CoffeeProperty<T>(val name: String, private var value: T, val serializer: IPropertySerializer<T>) :
    ReadWriteProperty<Any, T> {

    private var externalChangable = value is IChangable
    private var clientDependent = false

    var changed = false
        get() = externalChangable && (value as IChangable).changed || field
        set(value) {
            if (externalChangable) {
                (this.value as IChangable).changed = value
            }
            field = value
        }

    override operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.changed = true
        this.value = value
    }

    fun serialize(nbt: CompoundNBT) {
        serializer.serialize(name, value, nbt)
    }

    fun deserialize(nbt: CompoundNBT) {
        if (nbt.contains(name)) {
            this.value = serializer.deserialize(name, nbt)
            this.changed = false
        }
    }

    fun deserialize(nbt: CompoundNBT, fromClient: Boolean) {
        if (fromClient == clientDependent) {
            deserialize(nbt)
        }
    }

    fun clientDependent(): CoffeeProperty<T> {
        clientDependent = true
        return this
    }

    fun isClientDependent() = clientDependent
}