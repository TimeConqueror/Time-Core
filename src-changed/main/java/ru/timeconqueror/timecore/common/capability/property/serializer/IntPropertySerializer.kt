package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundNBT


object IntPropertySerializer : IPropertySerializer<Int> {

    override fun serialize(name: String, value: Int, nbt: CompoundNBT) = nbt.putInt(name, value)
    override fun deserialize(name: String, nbt: CompoundNBT) = nbt.getInt(name)

    object Nullable : NullPropertySerializer<Int>(this)
}