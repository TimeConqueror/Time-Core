package ru.timeconqueror.timecore.internal.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TKeyBinds {
    public static KeyMapping toggleReplaceMode;

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        toggleReplaceMode = new KeyMapping("key.timecore.toggle_replace_mode", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.timecore.category");
        event.register(toggleReplaceMode);
    }
}