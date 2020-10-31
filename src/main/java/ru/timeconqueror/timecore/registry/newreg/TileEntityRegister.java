package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class TileEntityRegister extends ForgeRegister<TileEntityType<?>> {
    public TileEntityRegister(String modid) {
        super(ForgeRegistries.TILE_ENTITIES, modid);
    }

    public <T extends TileEntity> TileEntityRegisterChain<T> register(String name, Supplier<T> tileEntityFactory, Block... validBlocks) {
        Supplier<TileEntityType<T>> typeSupplier = () ->
                TileEntityType.Builder.of(tileEntityFactory, validBlocks)
                        .build(null /*forge doesn't have support for it*/);

        RegistryObject<TileEntityType<T>> holder = registerEntry(name, typeSupplier);
        return new TileEntityRegisterChain<>(holder);
    }

    public class TileEntityRegisterChain<T extends TileEntity> extends ForgeRegister.RegisterChain<TileEntityType<T>> {
        public TileEntityRegisterChain(RegistryObject<TileEntityType<T>> holder) {
            super(TileEntityRegister.this, holder);
        }

        public TileEntityRegisterChain<T> regCustomRenderer(Supplier<Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>>> rendererFactory) {
            runTaskOnClientSetup(() -> ClientRegistry.bindTileEntityRenderer(asRegistryObject().get(), rendererFactory.get()));
            return this;
        }
    }
}
