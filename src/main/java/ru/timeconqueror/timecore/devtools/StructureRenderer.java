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
import net.minecraft.util.math.vector.Vector3d;
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
    private final Set<StructureData> trackedStructurePieces = new HashSet<>();

    private final Map<ResourceLocation, Integer> structureColorMap;
    private boolean visibleThroughBlocks;

    public StructureRenderer() {
        RevealerDataSaver.ClientSettings clientSettings = new RevealerDataSaver().restoreOnClient();
        structureColorMap = clientSettings.getStructureColorMap();
        visibleThroughBlocks = clientSettings.isVisibleThroughBlocks();
    }

    public void onWorldRender(RenderWorldLastEvent event) {
        ActiveRenderInfo activeRenderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vector3d position = activeRenderInfo.getPosition();

        RenderTypeBuffers renderTypeBuffers = Minecraft.getInstance().renderBuffers();
        IRenderTypeBuffer.Impl bufferSource = renderTypeBuffers.bufferSource();
        RenderType overlayRenderType = TimeRenderType.getOverlay(visibleThroughBlocks);
        IVertexBuilder buffer = bufferSource.getBuffer(overlayRenderType);

        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.pushPose();
        matrixStack.translate(-position.x, -position.y, -position.z);

        RenderSystem.disableCull();
        if (visibleThroughBlocks) {
            RenderSystem.disableDepthTest();
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        ResourceLocation worldId = player.level.dimension().location();
        int viewDistance = ((ViewDistanceProvider) player.connection).getServerChunkRadius() * 16 + 2 * 16 /*slight offset to not delete the structure info instantly*/;
        int viewDistanceSq = viewDistance * viewDistance;

        for (Iterator<StructureData> iterator = trackedStructurePieces.iterator(); iterator.hasNext(); ) {
            StructureData container = iterator.next();

            if (container.getWorldId() != worldId) {
                iterator.remove();
                continue;
            }

            double shortestDistanceSq = getShortestDistanceSq(player, container.getBoundingBox());
            if (shortestDistanceSq > viewDistanceSq) {
                iterator.remove();
            } else {
                DrawHelper.drawFilledBoundingBox(matrixStack, buffer, container.getBoundingBox(), DrawHelper.withChangedAlpha(getStructureColor(container.getStructureName()), 0x33));
            }
        }

        bufferSource.endBatch(overlayRenderType);

        RenderSystem.enableCull();
        if (visibleThroughBlocks) {
            RenderSystem.enableDepthTest();
        }

        matrixStack.popPose();
    }

    public void trackStructurePiece(StructureData structureData) {
        trackedStructurePieces.add(structureData);
    }

    public Set<StructureData> getTrackedStructurePieces() {
        return trackedStructurePieces;
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
        double x = MathUtils.coerceInRange(player.getX(), bb.minX, bb.maxX);
        double y = MathUtils.coerceInRange(player.getY(), bb.minY, bb.maxY);
        double z = MathUtils.coerceInRange(player.getZ(), bb.minZ, bb.maxZ);

        return player.distanceToSqr(x, y, z);
    }
}