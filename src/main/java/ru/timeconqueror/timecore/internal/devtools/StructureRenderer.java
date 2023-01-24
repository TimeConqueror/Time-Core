//package ru.timeconqueror.timecore.internal.devtools;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.AABB;
//import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
//import ru.timeconqueror.timecore.api.util.MathUtils;
//import ru.timeconqueror.timecore.api.util.RandHelper;
//import ru.timeconqueror.timecore.api.util.client.DrawHelper;
//
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//FIXME port
//public class StructureRenderer {
//    private final Set<StructureData> trackedStructurePieces = new HashSet<>();
//
//    private final Map<ResourceLocation, Integer> structureColorMap;
//    private boolean visibleThroughBlocks;
//
//    public StructureRenderer() {
//        RevealerDataSaver.ClientSettings clientSettings = new RevealerDataSaver().restoreOnClient();
//        structureColorMap = clientSettings.getStructureColorMap();
//        visibleThroughBlocks = clientSettings.isVisibleThroughBlocks();
//    }
//

////    public void onWorldRender(RenderWorldLastEvent event) {
////        if (!RenderHelper.isFabulousModeEnabled()) {
////            render(event.getMatrixStack(), false);
////        }
////    }
//
//    public void render(PoseStack stack, boolean fabulousMode) {

////        RenderSystem.pushMatrix();
////
////        if (fabulousMode) { // if we call it from WorldRenderer's mixin, we call it when matrix was multiplied, so we need to revert it
////            Matrix4f inverted = stack.last().pose().copy();
////            inverted.invert();
////            RenderSystem.multMatrix(inverted);
////        }
////
////        Camera activeRenderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
////        Vec3 position = activeRenderInfo.getPosition();
////
////        RenderBuffers renderTypeBuffers = Minecraft.getInstance().renderBuffers();
////        MultiBufferSource.BufferSource bufferSource = renderTypeBuffers.bufferSource();
////        RenderType overlayRenderType = TimeRenderTypes.getOverlay(visibleThroughBlocks);
////        VertexConsumer buffer = bufferSource.getBuffer(overlayRenderType);
////
////        stack.pushPose();
////        stack.translate(-position.x, -position.y, -position.z);
////
////        RenderSystem.disableCull();
////        if (visibleThroughBlocks) {
////            RenderSystem.disableDepthTest();
////        }
////
////        LocalPlayer player = Minecraft.getInstance().player;
////        ResourceLocation worldId = player.level.dimension().location();
////        int viewDistance = ((ViewDistanceProvider) player.connection).getServerChunkRadius() * 16 + 2 * 16 /*slight offset to not delete the structure info instantly*/;
////        int viewDistanceSq = viewDistance * viewDistance;
////
////        for (Iterator<StructureData> iterator = trackedStructurePieces.iterator(); iterator.hasNext(); ) {
////            StructureData container = iterator.next();
////
////            if (!container.getWorldId().equals(worldId)) {
////                iterator.remove();
////                continue;
////            }
////
////            double shortestDistanceSq = getShortestDistanceSq(player, container.getBoundingBox());
////            if (shortestDistanceSq > viewDistanceSq) {
////                iterator.remove();
////            } else {
////                DrawHelper.drawFilledBoundingBox(stack, buffer, container.getBoundingBox(), DrawHelper.changeAlpha(getStructureColor(container.getStructureName()), 0x55));
////            }
////        }
////
////        bufferSource.endBatch(overlayRenderType);
////
////        RenderSystem.enableCull();
////
////        if (visibleThroughBlocks) {
////            RenderSystem.enableDepthTest();
////        }
////
////        stack.popPose();
////        RenderSystem.popMatrix();
//    }
//
//    public void trackStructurePiece(StructureData structureData) {
//        trackedStructurePieces.add(structureData);
//    }
//
//    public Set<StructureData> getTrackedStructurePieces() {
//        return trackedStructurePieces;
//    }
//
//    public void setVisibleThroughBlocks(boolean visibleThroughBlocks) {
//        this.visibleThroughBlocks = visibleThroughBlocks;
//        save();
//    }
//
//    public void setStructureColor(ResourceLocation structureName, int color) {
//        structureColorMap.put(structureName, DrawHelper.opaquefy(color));
//        save();
//    }
//
//    private int getStructureColor(ResourceLocation structureName) {
//        Integer color = structureColorMap.get(structureName);
//        if (color == null) {
//            color = DrawHelper.opaquefy(RandHelper.RAND.nextInt());
//            structureColorMap.put(structureName, color);
//
//            save();
//        }
//
//        return color;
//    }
//
//    private void save() {
//        new RevealerDataSaver().saveOnClient(new RevealerDataSaver.ClientSettings(structureColorMap, visibleThroughBlocks));
//    }
//
//    public void onClientLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
//        trackedStructurePieces.clear();
//    }
//
//    private double getShortestDistanceSq(Player player, AABB bb) {
//        double x = MathUtils.coerceInRange(player.getX(), bb.minX, bb.maxX);
//        double y = MathUtils.coerceInRange(player.getY(), bb.minY, bb.maxY);
//        double z = MathUtils.coerceInRange(player.getZ(), bb.minZ, bb.maxZ);
//
//        return player.distanceToSqr(x, y, z);
//    }
//}