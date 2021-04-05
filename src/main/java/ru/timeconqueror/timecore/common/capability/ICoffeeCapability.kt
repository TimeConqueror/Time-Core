package ru.timeconqueror.timecore.common.capability

import net.minecraft.nbt.CompoundNBT
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.network.simple.SimpleChannel
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerSerializer
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import ru.timeconqueror.timecore.mod.common.packet.CoffeeCapabilityDataPacket
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager
import java.util.function.Predicate


interface ICoffeeCapability<T : ICapabilityProvider> {

    fun getProperties(): ArrayList<CoffeeProperty<*>>
    fun getOwnerSerializer(): CapabilityOwnerSerializer<T>
    fun getCapability(): Capability<out ICoffeeCapability<T>>

    fun serializeProperties(
        serializePredicate: Predicate<CoffeeProperty<*>>,
        nbt: CompoundNBT,
        clientSide: Boolean
    ): Boolean {
        var hasChanges = false
        for (property in getProperties()) {
            if (property.isClientDependent() == clientSide && serializePredicate.test(property)) {
                property.serialize(nbt)
                hasChanges = true
            }
        }
        return hasChanges
    }

    fun deserializeProperties(nbt: CompoundNBT) {
        for (property in getProperties()) {
            property.deserialize(nbt)
        }
    }

    fun deserializeProperties(nbt: CompoundNBT, fromClient: Boolean) {
        for (property in getProperties()) {
            property.deserialize(nbt, fromClient)
        }
    }

    fun createDataPacket(
        world: World,
        owner: T,
        clientSide: Boolean,
        syncPredicate: Predicate<CoffeeProperty<*>>
    ) = CoffeeCapabilityDataPacket.create(world, owner, this, clientSide, syncPredicate)

    /**
     * Checks if properties of capability has changed and if yes, sends them.
     * Works in both directions.
     */
    fun detectAndSendChanges(world: World, owner: T) {
        sendData(world, owner) { prop ->
            if (prop.changed) {
                prop.changed = false
                true
            } else false
        }
    }

    /**
     * Synchronizes all capability data.
     * Works in both directions.
     */
    fun sendAllData(world: World, owner: T) {
        sendData(world, owner) { true }
    }

    private fun sendData(world: World, owner: T, predicate: Predicate<CoffeeProperty<*>>) {
        val clientSide = world.isClientSide()

        val message = createDataPacket(world, owner, clientSide, predicate)

        if (message != null) {
            if (clientSide) {
                InternalPacketManager.INSTANCE.sendToServer(message)
            } else {
                onSendChangesToClients(InternalPacketManager.INSTANCE, message)
            }
        }
    }

    fun onSendChangesToClients(channel: SimpleChannel, data: Any) {}
}