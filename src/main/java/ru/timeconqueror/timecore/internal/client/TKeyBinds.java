package ru.timeconqueror.timecore.internal.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public class TKeyBinds {
    public static KeyMapping toggleReplaceMode;

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        toggleReplaceMode = new KeyMapping("key.timecore.toggle_replace_mode", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.timecore.category");
        event.register(toggleReplaceMode);
    }
}