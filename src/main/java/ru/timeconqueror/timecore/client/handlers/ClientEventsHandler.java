package ru.timeconqueror.timecore.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import ru.timeconqueror.timecore.mod.client.TKeyBinds;

@Mod.EventBusSubscriber
public class ClientEventsHandler {
    @SubscribeEvent
    public static void handleRawMouse(InputEvent.RawMouseEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && mc.player.isCreative() && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && event.getAction() == GLFW.GLFW_PRESS) {
            if (TKeyBinds.toggleReplaceMode.isDown()) {
                RayTraceResult hitResult = mc.hitResult;

                if (hitResult != null && hitResult.getType() == RayTraceResult.Type.BLOCK) {
                    BlockRayTraceResult blockTraceResult = (BlockRayTraceResult) hitResult;

                    BlockPos pos = blockTraceResult.getBlockPos();
                    if (!mc.level.isEmptyBlock(pos)) {
                        PlayerController controller = mc.gameMode;

                        controller.startDestroyBlock(pos, blockTraceResult.getDirection());
                        controller.useItemOn(mc.player, mc.level, Hand.MAIN_HAND, blockTraceResult);
                    }
                }

                event.setCanceled(true);
            }
        }
    }
}
