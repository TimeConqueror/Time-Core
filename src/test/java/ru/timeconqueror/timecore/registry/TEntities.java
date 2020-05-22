package ru.timeconqueror.timecore.registry;

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
import ru.timeconqueror.timecore.api.client.render.TimeEntityModel;
import ru.timeconqueror.timecore.api.client.render.animation.TimeClientLoader;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.entity.EntityFloro;
import ru.timeconqueror.timecore.entity.EntityZombie;
import ru.timeconqueror.timecore.entity.RenderFloro;
import ru.timeconqueror.timecore.entity.RenderZombie;

@TimeAutoRegistrable(target = TimeAutoRegistrable.Target.CLASS)
public class TEntities {
    public static final EntityType<? extends AnimalEntity> ZOMBIE_TYPE = EntityType.Builder.create(EntityZombie::new, EntityClassification.CREATURE)
            .setTrackingRange(80)
            .setShouldReceiveVelocityUpdates(true)
            .size(1, 2)
            .build(TimeCore.MODID + ":zombie");
    public static final EntityType<? extends AnimalEntity> FLORO = EntityType.Builder.create(EntityFloro::new, EntityClassification.CREATURE)
            .setTrackingRange(80)
            .setShouldReceiveVelocityUpdates(true)
            .size(1, 2)
            .build(TimeCore.MODID + ":floro");
    public static TimeEntityModel<EntityZombie> zombieModel;
    public static TimeEntityModel<EntityFloro> floroModel;
    public static IAnimation HIT_ANIMATION;
    public static IAnimation SCALING_ANIMATION;
    public static IAnimation OFFSETTING_ANIMATION;
    //    public static Animation FLORO_HIDING;
    public static IAnimation FLORO_SHOOT;
    public static IAnimation FLORO_SHOWING;
    public static IAnimation FLORO_WALK;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(ZOMBIE_TYPE.setRegistryName(TimeCore.MODID + ":zombie"));
        event.getRegistry().register(FLORO.setRegistryName(TimeCore.MODID + ":floro"));
    }

    @SubscribeEvent
    public static void registerSpawnEggs(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new SpawnEggItem(ZOMBIE_TYPE, 0xFF00FF00, 0xFF000000, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(TimeCore.MODID, "spawn_zombie"),
                new SpawnEggItem(FLORO, 0xFF00FF00, 0xFF000000, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(TimeCore.MODID, "spawn_floro")
        );
    }

    /**
     * Register Render Class for this Entity
     */
    @SubscribeEvent
    public static void registerRenders(FMLClientSetupEvent event) {
        zombieModel = TimeClientLoader.loadJsonEntityModel(new ResourceLocation(TimeCore.MODID, "models/entity/zombie.json"));
        floroModel = TimeClientLoader.loadJsonEntityModel(new ResourceLocation(TimeCore.MODID, "models/entity/floro.json"));
        HIT_ANIMATION = TimeClientLoader.loadAnimations(new ResourceLocation(TimeCore.MODID, "animations/zombie_hit.json")).get(0);
        SCALING_ANIMATION = TimeClientLoader.loadAnimations(new ResourceLocation(TimeCore.MODID, "animations/scaling.json")).get(0);
        OFFSETTING_ANIMATION = TimeClientLoader.loadAnimations(new ResourceLocation(TimeCore.MODID, "animations/offsetting.json")).get(0);
//        FLORO_HIDING = TimeClientLoader.loadAnimations(new ResourceLocation(TimeCore.MODID, "animations/floro.hiding.json")).get(0);
        FLORO_SHOOT = TimeClientLoader.loadAnimations(new ResourceLocation(TimeCore.MODID, "animations/floro.shoot.json")).get(0);
        FLORO_SHOWING = TimeClientLoader.loadAnimations(new ResourceLocation(TimeCore.MODID, "animations/floro.showing.json")).get(0);
        FLORO_WALK = TimeClientLoader.loadAnimations(new ResourceLocation(TimeCore.MODID, "animations/floro.walk.json")).get(0);
        RenderingRegistry.registerEntityRenderingHandler(EntityZombie.class, RenderZombie::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFloro.class, RenderFloro::new);
    }
}
