package ru.timeconqueror.timecore.mod.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class TKeyBinds {
    public static KeyBinding toggleReplaceMode;

    public static void registerKeys(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            toggleReplaceMode = new KeyBinding("Toggle Replace Mode", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "TimeCore");
            ClientRegistry.registerKeyBinding(toggleReplaceMode);
        });
    }
}