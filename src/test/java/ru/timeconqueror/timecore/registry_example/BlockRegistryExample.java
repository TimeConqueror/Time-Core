package ru.timeconqueror.timecore.registry_example;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;
import ru.timeconqueror.timecore.block.DummyBlockWithTileEntity;
import ru.timeconqueror.timecore.client.render.TimeRenderTypes;

import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@ObjectHolder(TimeCore.MODID)
public class BlockRegistryExample {
    public static final Block TEST_SAND = promise();
    public static final Block TEST_DIRT = promise();
    public static final Block TEST_EMERALD_ORE = promise();
    public static final Block TEST_BLOCK_WITH_TILE = promise();

    private static class Init {
        @AutoRegistrable
        private static final BlockRegister REGISTER = new BlockRegister(TimeCore.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            BlockPropsFactory propsCreator = new BlockPropsFactory(() -> Block.Properties.of(Material.STONE));

            REGISTER.register("test_sand", () -> new Block(propsCreator.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .also(chain -> {
                        BlockModelLocation modelLocation = new BlockModelLocation(chain.getModId(), "block/test_sand");
                        chain
                                .genModel(modelLocation, () -> BlockModels.cubeAllModel(new TextureLocation("minecraft", "block/sand")))
                                .genDefaultState(modelLocation);
                    });

            REGISTER.register("test_dirt", () -> new Block(propsCreator.create()))
                    .setRenderLayer(new TimeRenderTypes(null, null, 0, 0, false, false, () -> {
                    }, () -> {
                    }))
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/dirt"));

            REGISTER.register("test_emerald_ore", () -> new Block(propsCreator.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/emerald_ore"));

            REGISTER.register("test_block_with_tile", () -> new DummyBlockWithTileEntity(propsCreator.create()))
                    .regDefaultBlockItem(ItemGroup.TAB_MISC)
                    .genDefaultStateAndModel(new TextureLocation("minecraft", "block/furnace"));
        }
    }
}
