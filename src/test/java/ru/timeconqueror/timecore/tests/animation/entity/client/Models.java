package ru.timeconqueror.timecore.tests.animation.entity.client;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.model.TimeModelLoader;
import ru.timeconqueror.timecore.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.tests.animation.entity.client.render.RenderTowerGuardian;
import ru.timeconqueror.timecore.tests.animation.entity.entity.TowerGuardianEntity;
import ru.timeconqueror.timecore.tests.animation.entity.registry.EntityRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Models {
    public static TimeEntityModel<TowerGuardianEntity> towerGuardian;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenders(FMLClientSetupEvent event) {
        towerGuardian = TimeModelLoader.loadJsonEntityModel(new ResourceLocation(TimeCore.MODID, "models/entity/tower_guardian.json"));

        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.TOWER_GUARDIAN, RenderTowerGuardian::new);
    }
}
