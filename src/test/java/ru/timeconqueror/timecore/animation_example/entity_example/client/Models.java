package ru.timeconqueror.timecore.animation_example.entity_example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.entity_example.client.render.RenderFloro;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroEntity;
import ru.timeconqueror.timecore.animation_example.entity_example.registry.EntityRegistry;
import ru.timeconqueror.timecore.api.client.render.model.TimeModelLoader;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Models {
    public static TimeEntityModel<FloroEntity> floroModel;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenders(FMLClientSetupEvent event) {
        floroModel = TimeModelLoader.loadJsonEntityModel(new ResourceLocation(TimeCore.MODID, "models/entity/floro.json"));

        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.FLORO, RenderFloro::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.FLORO_PROJ, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}
