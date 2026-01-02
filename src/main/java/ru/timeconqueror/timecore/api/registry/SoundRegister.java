package ru.timeconqueror.timecore.api.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import ru.timeconqueror.timecore.api.TimeCoreAPI;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.Promised;

/**
 * All {@link TimeRegister}s are used to simplify stuff registering.
 * You can use it with {@link Promised} style.
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
 *     public class TileEntityDeferredRegistryExample {
 *         {@literal @}AutoRegistrable
 *          private static final TileEntityRegister REGISTER = new TileEntityRegister(TimeCore.MODID);
 *
 *          public static RegistryObject<TileEntityType<DummyTileEntity>> TEST_TE_TYPE = REGISTER.register("test_tile", DummyTileEntity::new, BlockRegistryExample.TEST_BLOCK_WITH_TILE)
 *              .regCustomRenderer(() -> DummyTileEntityRenderer::new) // <- one of extra features
 *              .asRegistryObject(); // <- retrieving registry object from our register chain.
 *      }
 *     </pre>
 * </blockquote>
 * <br>Examples can be seen at test module.
 */
public class SoundRegister extends VanillaRegister<SoundEvent> {
    public SoundRegister(String modid) {
        super(BuiltInRegistries.SOUND_EVENT, modid);
    }

    /**
     * Creates and registers sound with provided location
     *
     * @param location sound location.
     *                 It will be used as a part of registry key. Should NOT contain mod ID, because it will be bound automatically.
     */
    public Promised<SoundEvent> register(String location) {
        ResourceLocation registryName = ResourceLocation.fromNamespaceAndPath(getModId(), location);

        return registerEntry(location, () -> SoundEvent.createVariableRangeEvent(registryName));
    }
}
