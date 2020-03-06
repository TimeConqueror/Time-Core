package ru.timeconqueror.timecore.api.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Used for simplifying tile entity adding.<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link TimeAutoRegistrable}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 * Examples can be seen at test module.
 */
public abstract class TileEntityTimeRegistry extends WrappedForgeTimeRegistry<TileEntityType<?>> {
    private ArrayList<Supplier<Runnable>> rendererRegisterRunnables = new ArrayList<>();

    @SubscribeEvent
    public final void onRegTileEntitiesEvent(RegistryEvent.Register<TileEntityType<?>> event) {
        onFireRegistryEvent(event);
    }

    @SubscribeEvent
    public final void onClientSetupEvent(FMLClientSetupEvent event) {
        for (Supplier<Runnable> runnable : rendererRegisterRunnables) {
            runnable.get().run();
        }

        rendererRegisterRunnables.clear();
        rendererRegisterRunnables = null;
    }

    /**
     * Method to register tileEntities automatically.
     *
     * @param tileEntitySupplier supplier, that returns new TileEntity objects.
     * @param name               tile entity name.
     *                           It will be used as a part of registry key. Should NOT contain mod ID, because it will be bound automatically.
     * @param validBlocks        blocks, that can contain provided tile entity.
     * @return {@link TileEntityWrapper} to provide extra register options.
     */
    @SuppressWarnings("unchecked")
    public <T extends TileEntity> TileEntityWrapper<T> regTileEntity(Supplier<T> tileEntitySupplier, String name, Block... validBlocks) {
        TileEntityType<T> type = TileEntityType.Builder.create(tileEntitySupplier, validBlocks).build(null);
        return new TileEntityWrapper<>((Class<T>) tileEntitySupplier.get().getClass(), type, name);
    }

    /**
     * Method to register renderers for tileEntities.
     *
     * @param tileEntityClass  tile entity class, for which you want to apply special renderer.
     * @param rendererSupplier supplier, that should return instance of {@link TileEntityRenderer}.
     *                         Here we use double supplier to hide from java client classes.
     *                         If we don't do it, then it will crash on server side.
     * @param <T>              any class inherited from TileEntity.
     */
    public <T extends TileEntity> void regTileEntityRenderer(Class<T> tileEntityClass, Supplier<Supplier<TileEntityRenderer<T>>> rendererSupplier) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Supplier<Runnable> runnable = () -> () -> ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, rendererSupplier.get().get());
            rendererRegisterRunnables.add(runnable);
        }
    }

    public class TileEntityWrapper<T extends TileEntity> extends EntryWrapper {
        private Class<T> tileEntityClass;

        public TileEntityWrapper(Class<T> tileEntityClass, TileEntityType<T> entry, String name) {
            super(entry, name);
            this.tileEntityClass = tileEntityClass;
        }

        /**
         * Returns tile entity type bound to wrapper.
         * Method duplicates {@link #getEntry()}, so it exists only for easier understanding.
         */
        @SuppressWarnings("unchecked")
        public TileEntityType<T> retrieveTileEntityType() {
            return (TileEntityType<T>) getEntry();
        }

        /**
         * Method to register renderer for provided tile entity.
         *
         * @param rendererSupplier supplier, that should return instance of {@link TileEntityRenderer}.
         *                         Here we use double supplier to hide from java client classes.
         *                         If we don't do it, then it will crash on server side.
         */
        public TileEntityWrapper<T> regCustomRenderer(Supplier<Supplier<TileEntityRenderer<T>>> rendererSupplier) {
            regTileEntityRenderer(tileEntityClass, rendererSupplier);

            return this;
        }
    }
}
