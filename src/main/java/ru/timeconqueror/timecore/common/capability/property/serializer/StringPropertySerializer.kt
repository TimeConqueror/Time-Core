package ru.timeconqueror.timecore.common.capability.property.serializer

import net.minecraft.nbt.CompoundTag


object StringPropertySerializer : IPropertySerializer<String> {

    override fun serialize(name: String, value: String, nbt: CompoundTag) = nbt.putString(name, value)
    override fun deserialize(name: String, nbt: CompoundTag) = nbt.getString(name)

    object Nullable : NullPropertySerializer<String>(this)
}