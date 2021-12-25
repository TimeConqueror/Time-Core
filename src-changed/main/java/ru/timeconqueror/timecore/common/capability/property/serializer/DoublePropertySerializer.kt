package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundNBT


object DoublePropertySerializer : IPropertySerializer<Double> {

    override fun serialize(name: String, value: Double, nbt: CompoundNBT) = nbt.putDouble(name, value)
    override fun deserialize(name: String, nbt: CompoundNBT) = nbt.getDouble(name)

    object Nullable : NullPropertySerializer<Double>(this)
}