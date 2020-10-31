package ru.timeconqueror.timecore.registry.deferred;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.timecore.registry.deferred.base.DeferredFMLImplForgeRegister;
import ru.timeconqueror.timecore.util.Hacks;

import java.util.function.Function;
import java.util.function.Supplier;

public class DeferredTileEntityRegister extends DeferredFMLImplForgeRegister<TileEntityType<?>> {
    public DeferredTileEntityRegister(String modid) {
        super(ForgeRegistries.TILE_ENTITIES, modid);
    }

    public <T extends TileEntity> TileEntityRegistrator<T> regTileEntityType(String name, Supplier<T> tileEntityFactory, Supplier<Block[]> validBlocksSupplier) {
        Supplier<TileEntityType<?>> typeSupplier = () ->
                TileEntityType.Builder.of(tileEntityFactory, validBlocksSupplier.get())
                        .build(null /*forge doesn't have support for it*/);
        return new TileEntityRegistrator<T>(name, typeSupplier);
    }

    public class TileEntityRegistrator<T extends TileEntity> extends Registrator {
        protected TileEntityRegistrator(String name, Supplier<TileEntityType<?>> sup) {
            super(name, sup);
        }

        public TileEntityRegistrator<T> regCustomRenderer(Supplier<Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>>> rendererFactory) {
            runTaskOnClientSetup(() -> ClientRegistry.bindTileEntityRenderer(endTyped().get(), rendererFactory.get()));
            return this;
        }

        /**
         * The alternative of {@link #end()} method.
         * Will return the typed registry object of tile entity type.
         */
        public RegistryObject<TileEntityType<T>> endTyped() {
            return Hacks.bypassClassChecking(super.end());
        }
    }
}
