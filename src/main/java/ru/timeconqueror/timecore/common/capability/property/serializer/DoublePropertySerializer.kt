package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundTag


object DoublePropertySerializer : IPropertySerializer<Double> {

    override fun serialize(name: String, value: Double, nbt: CompoundTag) = nbt.putDouble(name, value)
    override fun deserialize(name: String, nbt: CompoundTag) = nbt.getDouble(name)

    object Nullable : NullPropertySerializer<Double>(this)
}