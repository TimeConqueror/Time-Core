package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundTag


object FloatPropertySerializer : IPropertySerializer<Float> {

    override fun serialize(name: String, value: Float, nbt: CompoundTag) = nbt.putFloat(name, value)
    override fun deserialize(name: String, nbt: CompoundTag) = nbt.getFloat(name)

    object Nullable : NullPropertySerializer<Float>(this)
}