package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundNBT


object FloatPropertySerializer : IPropertySerializer<Float> {

    override fun serialize(name: String, value: Float, nbt: CompoundNBT) = nbt.putFloat(name, value)
    override fun deserialize(name: String, nbt: CompoundNBT) = nbt.getFloat(name)

    object Nullable : NullPropertySerializer<Float>(this)
}