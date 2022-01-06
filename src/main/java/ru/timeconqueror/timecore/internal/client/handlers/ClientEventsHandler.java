package ru.timeconqueror.timecore.internal.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import ru.timeconqueror.timecore.internal.client.TKeyBinds;

@Mod.EventBusSubscriber
public class ClientEventsHandler {
    @SubscribeEvent
    public static void handleRawMouse(InputEvent.RawMouseEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && mc.player.isCreative() && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && event.getAction() == GLFW.GLFW_PRESS) {
            if (TKeyBinds.toggleReplaceMode.isDown()) {
                HitResult hitResult = mc.hitResult;

                if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockTraceResult = (BlockHitResult) hitResult;

                    BlockPos pos = blockTraceResult.getBlockPos();
                    if (!mc.level.isEmptyBlock(pos)) {
                        MultiPlayerGameMode controller = mc.gameMode;

                        controller.startDestroyBlock(pos, blockTraceResult.getDirection());
                        controller.useItemOn(mc.player, mc.level, InteractionHand.MAIN_HAND, blockTraceResult);
                    }
                }

                event.setCanceled(true);
            }
        }
    }
}
