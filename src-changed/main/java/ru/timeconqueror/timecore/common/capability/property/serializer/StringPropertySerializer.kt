package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundNBT


object StringPropertySerializer : IPropertySerializer<String> {

    override fun serialize(name: String, value: String, nbt: CompoundNBT) = nbt.putString(name, value)
    override fun deserialize(name: String, nbt: CompoundNBT) = nbt.getString(name)

    object Nullable : NullPropertySerializer<String>(this)
}