package ru.timeconqueror.timecore.common.capability

import net.minecraft.nbt.CompoundNBT
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.network.simple.SimpleChannel
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerSerializer
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import ru.timeconqueror.timecore.mod.common.packet.CoffeeCapabilityDataMsg
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager
import java.util.function.Predicate


interface ICoffeeCapability<T : ICapabilityProvider> {

    fun getProperties(): ArrayList<CoffeeProperty<*>>
    fun getOwnerSerializer(): CapabilityOwnerSerializer<T>
    fun getCapability(): Capability<out ICoffeeCapability<T>>

    fun serializeProperties(predicate: Predicate<CoffeeProperty<*>>, nbt: CompoundNBT, clientSide: Boolean): Boolean {
        var hasChanges = false
        for (property in getProperties()) {
            if (property.isClientDependent() == clientSide && predicate.test(property)) {
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

    fun createDataMessageIfHasChanges(
        world: World,
        owner: T,
        clientSide: Boolean,
        predicate: Predicate<CoffeeProperty<*>>
    ) = CoffeeCapabilityDataMsg.createIfHasChanges(world, owner, this, clientSide, predicate)

    fun detectAndSendChanges(world: World, owner: T) {
        val clientSide = world.isClientSide()

        val message = createDataMessageIfHasChanges(world, owner, clientSide, Predicate { prop ->
            if (prop.changed) {
                prop.changed = false
                true
            } else false
        })

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