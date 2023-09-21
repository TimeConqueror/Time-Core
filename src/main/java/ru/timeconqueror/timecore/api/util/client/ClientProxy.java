package ru.timeconqueror.timecore.api.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import ru.timeconqueror.timecore.api.util.Hacks;

/**
 * Safe alternatives to client stuff, which will only crash if you use it on the wrong side.
 * It won't crash upon class loading.
 */
public class ClientProxy {
    public static Player player() {
        return Hacks.safeCast(Minecraft.getInstance().player);
    }

    public static Level level() {
        return Hacks.safeCast(Minecraft.getInstance().level);
    }
}
