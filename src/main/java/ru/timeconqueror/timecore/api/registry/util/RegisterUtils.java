package ru.timeconqueror.timecore.api.registry.util;

import net.minecraft.world.level.block.Block;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.BlockRegister;

public class RegisterUtils {
    public static BlockModelLocation defaultBml(BlockRegister.BlockRegisterChain<? extends Block> chain) {
        return new BlockModelLocation(chain.getModId(), chain.getName());
    }

    public static TextureLocation defaultBlockTl(BlockRegister.BlockRegisterChain<? extends Block> chain) {
        return new TextureLocation(chain.getModId(), "block/" + chain.getName());
    }

    public static TextureLocation defaultItemTl(BlockRegister.BlockRegisterChain<? extends Block> chain) {
        return new TextureLocation(chain.getModId(), "item/" + chain.getName());
    }
}
