package ru.timeconqueror.timecore.animation_example.entity_example.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.entity_example.client.render.RenderFloro;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroDirtProjectileEntity;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroEntity;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.client.render.model.TimeModelLoader;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    public static final EntityType<FloroEntity> FLORO_TYPE = EntityType.Builder.of(FloroEntity::new, EntityClassification.MONSTER)
            .setTrackingRange(80)
            .setShouldReceiveVelocityUpdates(true)
            .sized(1, 2)
            .build(TimeCore.MODID + ":floro");
    public static final EntityType<FloroDirtProjectileEntity> FLORO_PROJECTILE_TYPE = EntityType.Builder.<FloroDirtProjectileEntity>of(FloroDirtProjectileEntity::new, EntityClassification.MISC)
            .setTrackingRange(80)
            .setShouldReceiveVelocityUpdates(true)
            .sized(0.5F, 0.5F)
            .build(TimeCore.MODID + ":floro_proj");

    public static TimeEntityModel<FloroEntity> floroModel;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(FLORO_TYPE.setRegistryName(TimeCore.MODID + ":floro"));
        event.getRegistry().register(FLORO_PROJECTILE_TYPE.setRegistryName(TimeCore.MODID + ":floro_proj"));

        GlobalEntityTypeAttributes.put(FLORO_TYPE, FloroEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnEggs(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new SpawnEggItem(FLORO_TYPE, 0xFF00FF00, 0xFF000000, new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(TimeCore.MODID, "spawn_floro")
        );
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenders(FMLClientSetupEvent event) {
        floroModel = TimeModelLoader.loadJsonEntityModel(new ResourceLocation(TimeCore.MODID, "models/entity/floro.json"));

        RenderingRegistry.registerEntityRenderingHandler(FLORO_TYPE, RenderFloro::new);
        RenderingRegistry.registerEntityRenderingHandler(FLORO_PROJECTILE_TYPE, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}
