package ru.timeconqueror.timecore.registry;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.TimeClientLoader;
import ru.timeconqueror.timecore.api.client.render.TimeEntityModel;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.entity.EntityZombie;
import ru.timeconqueror.timecore.entity.RenderZombie;

@TimeAutoRegistrable(target = TimeAutoRegistrable.Target.CLASS)
public class ModEntities {
    public static final EntityType<? extends AnimalEntity> ZOMBIE_TYPE = EntityType.Builder.create(EntityZombie::new, EntityClassification.CREATURE)
            .setTrackingRange(80)
            .setShouldReceiveVelocityUpdates(true)
            .size(1, 2)
            .build(TimeCore.MODID + ":zombie");
    public static TimeEntityModel<EntityZombie> zombieModel;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<EntityType<?>> event) {
        ZOMBIE_TYPE.setRegistryName(TimeCore.MODID + ":zombie");
        event.getRegistry().register(ZOMBIE_TYPE);
    }

    @SubscribeEvent
    public static void registerSpawnEggs(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new SpawnEggItem(ZOMBIE_TYPE, 0xFF00FF00, 0xFF000000, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(TimeCore.MODID, "spawn_zombie")
        );
    }

    /**
     * Register Render Class for this Entity
     */
    @SubscribeEvent
    public static void registerRenders(FMLClientSetupEvent event) {
        zombieModel = TimeClientLoader.loadJsonEntityModel(new ResourceLocation(TimeCore.MODID, "models/zombie.json"));
        RenderingRegistry.registerEntityRenderingHandler(EntityZombie.class, ModEntities::createRenderFor);
    }

    private static EntityRenderer<? super EntityZombie> createRenderFor(EntityRendererManager manager) {
        try {
            return new RenderZombie(manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
