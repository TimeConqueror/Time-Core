package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundNBT


object BooleanPropertySerializer : IPropertySerializer<Boolean> {

    override fun serialize(name: String, value: Boolean, nbt: CompoundNBT) = nbt.putBoolean(name, value)
    override fun deserialize(name: String, nbt: CompoundNBT) = nbt.getBoolean(name)

    object Nullable : NullPropertySerializer<Boolean>(this)
}