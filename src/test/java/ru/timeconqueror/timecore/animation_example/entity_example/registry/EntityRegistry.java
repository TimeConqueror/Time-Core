package ru.timeconqueror.timecore.animation_example.entity_example.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.entity_example.client.render.RenderFloro;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroDirtProjectileEntity;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroEntity;
import ru.timeconqueror.timecore.api.client.render.model.TimeModelLoader;
import ru.timeconqueror.timecore.api.registry.EntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    @AutoRegistrable
    private static final EntityRegister REGISTER = new EntityRegister(TimeCore.MODID);

    public static final EntityType<FloroEntity> FLORO = REGISTER.registerLiving("floro",
            EntityType.Builder.of(FloroEntity::new, EntityClassification.MONSTER)
                    .setTrackingRange(80)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(1, 2)
    )
            .attributes(() -> FloroEntity.createAttributes().build())
            .spawnEgg(0xFF00FF00, 0xFF000000, ItemGroup.TAB_MISC)
            .retrieve();
    public static final EntityType<FloroDirtProjectileEntity> FLORO_PROJ = REGISTER.register("floro_proj",
            EntityType.Builder.<FloroDirtProjectileEntity>of(FloroDirtProjectileEntity::new, EntityClassification.MISC)
                    .setTrackingRange(80)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(0.5F, 0.5F))
            .retrieve();

    public static TimeEntityModel<FloroEntity> floroModel;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenders(FMLClientSetupEvent event) {
        floroModel = TimeModelLoader.loadJsonEntityModel(new ResourceLocation(TimeCore.MODID, "models/entity/floro.json"));

        RenderingRegistry.registerEntityRenderingHandler(FLORO, RenderFloro::new);
        RenderingRegistry.registerEntityRenderingHandler(FLORO_PROJ, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}
