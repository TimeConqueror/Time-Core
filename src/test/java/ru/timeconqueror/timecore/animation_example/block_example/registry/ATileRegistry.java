package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.block_example.block.tile.TileHeatCube;
import ru.timeconqueror.timecore.animation_example.block_example.client.render.TERHeatCube;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.newreg.TileEntityRegister;

import static ru.timeconqueror.timecore.util.Hacks.promise;

@ObjectHolder(TimeCore.MODID)
public class ATileRegistry {
    public static final TileEntityType<TileHeatCube> HEAT_CUBE = promise();

    private static class Init {
        @AutoRegistrable
        private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            REGISTER.registerSingleBound("heat_cube", TileHeatCube::new, () -> ABlockRegistry.HEAT_CUBE)
                    .regCustomRenderer(() -> TERHeatCube::new);
        }
    }
}
