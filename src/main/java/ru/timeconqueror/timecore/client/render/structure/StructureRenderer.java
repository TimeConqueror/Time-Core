package ru.timeconqueror.timecore.client.render.structure;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class StructureRenderer {
    @SubscribeEvent
    public static void onRenderLast(RenderWorldLastEvent event) {

    }
}
