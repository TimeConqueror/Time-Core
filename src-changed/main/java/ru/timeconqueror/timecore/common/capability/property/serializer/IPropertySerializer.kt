package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundNBT


interface IPropertySerializer<T> {

    fun serialize(name: String, value: T, nbt: CompoundNBT)

    fun deserialize(name: String, nbt: CompoundNBT): T

    fun nullable() = NullPropertySerializer(this)
}