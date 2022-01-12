package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.block_example.block.tile.TileHeatCube;
import ru.timeconqueror.timecore.animation_example.block_example.client.render.TERHeatCube;
import ru.timeconqueror.timecore.api.registry.TileEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@ObjectHolder(TimeCore.MODID)
public class ATileRegistry {
    public static final BlockEntityType<TileHeatCube> HEAT_CUBE = promise();

    private static class Init {
        @AutoRegistrable
        private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);

        @AutoRegistrable.Init
        private static void register() {
            REGISTER.registerSingleBound("heat_cube", TileHeatCube::new, () -> ABlockRegistry.HEAT_CUBE)
                    .regCustomRenderer(() -> TERHeatCube::new);
        }
    }
}
