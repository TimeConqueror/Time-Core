package ru.timeconqueror.timecore.api.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import ru.timeconqueror.timecore.api.util.Hacks;

/**
 * Safe alternatives to common client stuff, which will only crash if you use it on the wrong side.
 * It won't crash upon class loading.
 */
public class ClientProxy {
    public static PlayerEntity player() {
        return Hacks.safeCast(Minecraft.getInstance().player);
    }

    public static World world() {
        return Hacks.safeCast(Minecraft.getInstance().level);
    }
}
