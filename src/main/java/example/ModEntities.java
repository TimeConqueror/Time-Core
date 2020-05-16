package example;

import example.json.EntityZombie;
import example.json.RenderZombie;
import example.obj.EntityPhoenix;
import example.obj.RenderPhoenix;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.TimeClientLoader;
import ru.timeconqueror.timecore.api.client.render.TimeEntityModel;
import ru.timeconqueror.timecore.client.render.animation.Animation;

public class ModEntities {
    public static TimeEntityModel zombieModel;
    public static Animation hitAnimation;
    public static Animation scalingAnimation;
    public static Animation offsettingAnimation;

    public static void register() {
        int id = 0;

        registerEntity("phoenix", EntityPhoenix.class, id++, 80, 3, true, 0xe2bf0d, 0xed6c09);
        registerEntity("zombie", EntityZombie.class, id++, 80, 3, true, 0xe2ffff, 0xff6cff);
    }

    /**
     * Register Render Class for this Entity
     */
    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityPhoenix.class, RenderPhoenix::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityZombie.class, RenderZombie::new);
        zombieModel = TimeClientLoader.loadJsonEntityModel(new ResourceLocation(TimeCore.MODID, "models/entity/zombie.json"));
        hitAnimation = TimeClientLoader.loadAnimation(new ResourceLocation(TimeCore.MODID, "animations/zombie_hit.json")).get(0);
        scalingAnimation = TimeClientLoader.loadAnimation(new ResourceLocation(TimeCore.MODID, "animations/scaling.json")).get(0);
        offsettingAnimation = TimeClientLoader.loadAnimation(new ResourceLocation(TimeCore.MODID, "animations/offsetting.json")).get(0);
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
}
