package ru.timeconqueror.timecore.api.auxiliary

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.common.FMLCommonHandler
import java.util.stream.Collectors

object NetworkUtils {
    /**
     * Sends message to all players in given distance.
     *
     * @param distanceIn distance from `fromPos`, in which players will be get a message.
     */
    @JvmStatic
    fun sendMessageToAllNearby(fromPos: BlockPos, msg: ITextComponent, distanceIn: Double) {
        for (entityPlayerMP in getPlayersNearby(fromPos, distanceIn)) {
            entityPlayerMP.sendMessage(msg)
        }
    }

    /**
     * Returns all players that are in specific distance from given pos.
     */
    @JvmStatic
    fun getPlayersNearby(fromPos: BlockPos, distanceIn: Double): List<EntityPlayerMP> {
        val players = FMLCommonHandler.instance().minecraftServerInstance.playerList.players
        return players.stream().filter { player: EntityPlayerMP ->
            val distance = player.getDistance(fromPos.x.toDouble(), fromPos.y.toDouble(), fromPos.z.toDouble())
            distance <= distanceIn
        }.collect(Collectors.toList())
    }
}