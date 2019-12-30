package example;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.timecore.TimeCore;

public class ModEntities {

    public static void register() {
        int id = 0;

        registerEntity("phoenix", EntityPhoenix.class, id++, 80, 3, true, 0xe2bf0d, 0xed6c09);
    }

    /**
     * Register Render Class for this Entity
     */
    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityPhoenix.class, ModEntities::createRenderFor);
    }

    /**
     * Register entity without spawn egg.
     */
    public static void registerEntity(String name, Class<? extends Entity> entityClass, int id, int trackingRange, int updateFrequency, boolean sendVelocityUpdates) {
        EntityRegistry.registerModEntity(new ResourceLocation(TimeCore.MODID + ":" + name), entityClass, name, id, TimeCore.instance, trackingRange, updateFrequency, sendVelocityUpdates);
    }

    /**
     * Register entity with spawn egg.
     */
    public static void registerEntity(String name, Class<? extends Entity> entityClass, int id, int trackingRange, int updateFrequency, boolean sendVelocityUpdates, int primaryEggColor, int secondaryEggColor) {
        EntityRegistry.registerModEntity(new ResourceLocation(TimeCore.MODID + ":" + name), entityClass, name, id, TimeCore.instance, trackingRange, updateFrequency, sendVelocityUpdates, primaryEggColor, secondaryEggColor);
    }

    private static Render<? super EntityPhoenix> createRenderFor(RenderManager manager) {
        try {
            return new RenderPhoenix(manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
