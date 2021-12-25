package ru.timeconqueror.timecore.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class TKeyBinds {
    public static KeyMapping toggleReplaceMode;

    public static void registerKeys(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            toggleReplaceMode = new KeyMapping("Toggle Replace Mode", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "TimeCore");//TODO I18n
            ClientRegistry.registerKeyBinding(toggleReplaceMode);
        });
    }
}