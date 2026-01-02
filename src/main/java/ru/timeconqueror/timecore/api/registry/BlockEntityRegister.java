package ru.timeconqueror.timecore.api.registry;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import ru.timeconqueror.timecore.api.TimeCoreAPI;
import ru.timeconqueror.timecore.api.client.render.blockentity.ProfiledBlockEntityRenderer;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.Promised;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * All {@link TimeRegister}s are used to simplify stuff registering.
 * You can use it with {@link Promised}  style.
 * <p>
 * To use it you need to:
 * <ol>
 *     <li>Create its instance and declare it static. Access modifier can be any.</li>
 *     <li>Attach {@link AutoRegistrable} annotation to it to register it as an event listener.</li>
 *     <li>Call {@link TimeCoreAPI#setup(Object)} from your mod constructor to enable TimeCore's annotations.</li>
 * </ol>
 *
 * <b>Features:</b>
 * If you need to register stuff, your first step will be to call method #register.
 * If the register system has any extra available registering stuff, then this method will return Register Chain,
 * which will have extra methods to apply.
 * Otherwise it will RegistryObject, which can be used or not used (depending on your registry style).
 * <br>
 * <br>
 * <b>{@link Promised} style:</b>
 * <br>
 * <blockquote>
 *     <pre>
 *     public class BlockEntityDeferredRegistryExample {
 *         {@literal @}AutoRegistrable
 *          private static final BlockEntityRegister REGISTER = new BlockEntityRegister(TimeCore.MODID);
 *
 *          public static RegistryObject<BlockEntityType<DummyBlockEntity>> TEST_TE_TYPE = REGISTER.register("test_block_entity", DummyBlockEntity::new, BlockRegistryExample.TEST_BLOCK_WITH_ENTITY)
 *              .regCustomRenderer(() -> DummyBlockEntityRenderer::new) // <- one of extra features
 *              .asRegistryObject(); // <- retrieving registry object from our register chain.
 *      }
 *     </pre>
 * </blockquote>
 * <br> Examples can be seen at test module.
 */
public class BlockEntityRegister extends VanillaRegister<BlockEntityType<?>> {
    public BlockEntityRegister(String modid) {
        super(Registries.BLOCK_ENTITY_TYPE, modid);
    }

    /**
     * Adds entry in provided {@code entrySup} to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link BlockEntityRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All method of {@link BlockEntityRegisterChain} are optional.
     *
     * @param name               The block type's name, will automatically have the modid as a namespace.
     * @param blockEntityFactory A factory for the new block entity, it should return a new instance every time it is called.
     * @param validBlock         block, which can have entity type.
     * @return A {@link BlockEntityRegisterChain} for adding some extra stuff.
     * @see BlockEntityRegisterChain
     */
    public <T extends BlockEntity> BlockEntityRegisterChain<T> registerSingleBound(String name, BlockEntityType.BlockEntitySupplier<T> blockEntityFactory, Supplier<Block> validBlock) {
        return register(name, blockEntityFactory, () -> Lists.newArrayList(validBlock.get()));
    }

    /**
     * Adds entry in provided {@code entrySup} to the queue, all entries from which will be registered later.
     * <p>
     * This method also returns {@link BlockEntityRegisterChain} to provide extra methods, which you can apply to entry being registered.
     * All methods of {@link BlockEntityRegisterChain} are optional.
     *
     * @param name               The block entity type's name, will automatically have the modid as a namespace.
     * @param blockEntityFactory A factory for the new block entity, it should return a new instance every time it is called.
     * @param validBlocks        blocks, which can have this entity type.
     * @return A {@link BlockEntityRegisterChain} for adding some extra stuff.
     * @see BlockEntityRegisterChain
     */
    public <T extends BlockEntity> BlockEntityRegisterChain<T> register(String name, BlockEntityType.BlockEntitySupplier<T> blockEntityFactory, Supplier<List<Block>> validBlocks) {
        Supplier<BlockEntityType<T>> typeSupplier = () ->
                BlockEntityType.Builder.of(blockEntityFactory, validBlocks.get().toArray(new Block[0]))
                        .build(null /*forge doesn't have support for it*/);

        Promised<BlockEntityType<T>> holder = registerEntry(name, typeSupplier);
        return new BlockEntityRegisterChain<>(holder);
    }

    public class BlockEntityRegisterChain<T extends BlockEntity> extends RegisterChain<BlockEntityType<T>> {
        private BlockEntityRegisterChain(Promised<BlockEntityType<T>> holder) {
            super(holder);
        }

        public BlockEntityRegisterChain<T> regCustomRenderer(Supplier<Function<? super BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T>>> rendererFactory) {
            clientSideOnly(() -> BlockEntityRegister.regCustomRenderer(BlockEntityRegister.this, asPromised(), rendererFactory));
            return this;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static <T extends BlockEntity> void regCustomRenderer(BlockEntityRegister register, Promised<BlockEntityType<T>> registryObject, Supplier<Function<? super BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T>>> rendererFactory) {
        register.runOnClientSetup(() -> {
            BlockEntityRenderers.register(registryObject.get(), context_ -> new ProfiledBlockEntityRenderer<>(context_, rendererFactory.get()));
        });
    }
}
