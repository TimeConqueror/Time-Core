package ru.timeconqueror.timecore.animation_example.entity_example.registry;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.entity_example.client.render.RenderFloro;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroEntity;
import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroMudEntity;
import ru.timeconqueror.timecore.api.registry.EntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.util.Hacks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@AutoRegistrable.Entries(value = TimeCore.MODID, registryKey = "entity_type")
public class EntityRegistry {
    @AutoRegistrable
    private static final EntityRegister REGISTER = new EntityRegister(TimeCore.MODID);

    public static EntityType<FloroEntity> FLORO = Hacks.promise();
    public static EntityType<FloroMudEntity> FLORO_PROJ = Hacks.promise();

    @AutoRegistrable.Init()
    private static void register() {
        REGISTER.registerMob("floro",
                        EntityType.Builder.of(FloroEntity::new, MobCategory.MONSTER)
                                .setTrackingRange(80)
                                .setShouldReceiveVelocityUpdates(true)
                                .sized(1, 2)
                )
          //      .spawnEgg(0xFF00FF00, 0xFF000000, CreativeModeTabs.TOOLS_AND_UTILITIES)
                .attributes(() -> FloroEntity.createAttributes().build());

        REGISTER.register("floro_proj",
                EntityType.Builder.<FloroMudEntity>of(FloroMudEntity::new, MobCategory.MISC)
                        .setTrackingRange(80)
                        .setShouldReceiveVelocityUpdates(true)
                        .sized(0.5F, 0.5F));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.FLORO, RenderFloro::new);
        event.registerEntityRenderer(EntityRegistry.FLORO_PROJ, ThrownItemRenderer::new);
    }
}
