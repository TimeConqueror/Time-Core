package ru.timeconqueror.timecore.devtools;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.RandHelper;
import ru.timeconqueror.timecore.client.render.TimeRenderType;
import ru.timeconqueror.timecore.mod.mixins.accessor.client.ViewDistanceProvider;
import ru.timeconqueror.timecore.util.client.DrawHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StructureRenderer {
    private final Set<StructurePieceContainer> trackedStructurePieces = new HashSet<>();

    private final Map<ResourceLocation, Integer> structureColorMap;
    private boolean visibleThroughBlocks;

    public StructureRenderer() {
        RevealerDataSaver.ClientSettings clientSettings = new RevealerDataSaver().restoreOnClient();
        structureColorMap = clientSettings.getStructureColorMap();
        visibleThroughBlocks = clientSettings.isVisibleThroughBlocks();
    }

    public void onWorldRender(RenderWorldLastEvent event) {
        ActiveRenderInfo activeRenderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        Vec3d projectedView = activeRenderInfo.getProjectedView();

        RenderTypeBuffers renderTypeBuffers = Minecraft.getInstance().getRenderTypeBuffers();
        IRenderTypeBuffer.Impl bufferSource = renderTypeBuffers.getBufferSource();
        RenderType overlayRenderType = TimeRenderType.getOverlay(visibleThroughBlocks);
        IVertexBuilder buffer = bufferSource.getBuffer(overlayRenderType);

        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.push();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        RenderSystem.disableCull();
        if (visibleThroughBlocks) {
            RenderSystem.disableDepthTest();
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        int currentDimension = player.world.getDimension().getType().getId();
        int viewDistance = ((ViewDistanceProvider) player.connection).getViewDistance() * 16 + 2 * 16 /*slight offset to not delete the structure info instantly*/;
        int viewDistanceSq = viewDistance * viewDistance;

        for (Iterator<StructurePieceContainer> iterator = trackedStructurePieces.iterator(); iterator.hasNext(); ) {
            StructurePieceContainer container = iterator.next();

            if (container.getDimension() != currentDimension) {
                iterator.remove();
                continue;
            }

            double shortestDistanceSq = getShortestDistanceSq(player, container.getBb());
            if (shortestDistanceSq > viewDistanceSq) {
                iterator.remove();
            } else {
                DrawHelper.drawFilledBoundingBox(matrixStack, buffer, container.getBb(), DrawHelper.withChangedAlpha(getStructureColor(container.getStructureName()), 0x33));
            }
        }

        bufferSource.finish(overlayRenderType);

        RenderSystem.enableCull();
        if (visibleThroughBlocks) {
            RenderSystem.enableDepthTest();
        }

        matrixStack.pop();
    }

    public void trackStructurePiece(ResourceLocation structureName, AxisAlignedBB bb, int dimension) {
        trackedStructurePieces.add(new StructurePieceContainer(structureName, bb, dimension));
    }

    public void setVisibleThroughBlocks(boolean visibleThroughBlocks) {
        this.visibleThroughBlocks = visibleThroughBlocks;
        save();
    }

    public void setStructureColor(ResourceLocation structureName, int color) {
        structureColorMap.put(structureName, DrawHelper.opaquefy(color));
        save();
    }

    private int getStructureColor(ResourceLocation structureName) {
        Integer color = structureColorMap.get(structureName);
        if (color == null) {
            color = DrawHelper.opaquefy(RandHelper.RAND.nextInt());
            structureColorMap.put(structureName, color);

            save();
        }

        return color;
    }

    private void save() {
        new RevealerDataSaver().saveOnClient(new RevealerDataSaver.ClientSettings(structureColorMap, visibleThroughBlocks));
    }

    public void onClientLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        trackedStructurePieces.clear();
    }

    private double getShortestDistanceSq(PlayerEntity player, AxisAlignedBB bb) {
        double x = MathUtils.coerceInRange(player.getPosX(), bb.minX, bb.maxX);
        double y = MathUtils.coerceInRange(player.getPosY(), bb.minY, bb.maxY);
        double z = MathUtils.coerceInRange(player.getPosZ(), bb.minZ, bb.maxZ);

        return player.getDistanceSq(x, y, z);
    }
}