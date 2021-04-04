package ru.timeconqueror.timecore.mod.common.packet

import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.PacketBuffer
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.network.NetworkEvent
import ru.timeconqueror.timecore.TimeCore
import ru.timeconqueror.timecore.api.common.packet.ITimePacket
import ru.timeconqueror.timecore.common.capability.ICoffeeCapability
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty
import java.util.function.Predicate

class CoffeeCapabilityDataMsg(val capabilityName: String, val ownerData: CompoundNBT, val capabilityData: CompoundNBT) :
    ITimePacket {
    companion object {
        fun <T : ICapabilityProvider> create(
            world: World,
            owner: T,
            cap: ICoffeeCapability<T>,
            capabilityData: CompoundNBT
        ): CoffeeCapabilityDataMsg {
            return CoffeeCapabilityDataMsg(cap.getCapability().name, CompoundNBT().apply {
                cap.getOwnerSerializer().serializeOwner(world, owner, this)
            }, capabilityData)
        }

        fun <T : ICapabilityProvider> createIfHasChanges(
            world: World,
            owner: T,
            cap: ICoffeeCapability<T>,
            clientSide: Boolean,
            predicate: Predicate<CoffeeProperty<*>>
        ): CoffeeCapabilityDataMsg? {
            val nbt = CompoundNBT()
            return if (cap.serializeProperties(predicate, nbt, clientSide)) {
                create(world, owner, cap, nbt)
            } else null
        }

        private fun handlePacket(msg: CoffeeCapabilityDataMsg, world: World, fromClient: Boolean) {
            val capability = TimeCore.INSTANCE.capabilityManager.getAttachableCoffeeCapability(msg.capabilityName)
            if (capability != null) {

                val ownerSerializer = capability.owner.serializer
                val owner: ICapabilityProvider? = ownerSerializer.deserializeOwner(world, msg.ownerData)

                if (owner != null) {
                    val cap: LazyOptional<out ICoffeeCapability<*>> = owner.getCapability(capability.capability, null)

                    cap.ifPresent {
                        it.deserializeProperties(msg.capabilityData, fromClient)
                    }
                }
            }
        }
    }

    object Handler : ITimePacket.ITimePacketHandler<CoffeeCapabilityDataMsg> {

        override fun encode(dataMsg: CoffeeCapabilityDataMsg, buf: PacketBuffer) {
            buf.writeUtf(dataMsg.capabilityName)
            buf.writeNbt(dataMsg.ownerData)
            buf.writeNbt(dataMsg.capabilityData)
        }

        override fun decode(buf: PacketBuffer): CoffeeCapabilityDataMsg {
            return CoffeeCapabilityDataMsg(buf.readUtf(), buf.readNbt()!!, buf.readNbt()!!)
        }

        override fun handle(msg: CoffeeCapabilityDataMsg, ctx: NetworkEvent.Context): Boolean {
            val fromClient = ctx.direction.receptionSide == LogicalSide.SERVER

            ctx.enqueueWork {
                handlePacket(msg, getWorld(ctx), fromClient)
            }

            return true
        }

    }
}