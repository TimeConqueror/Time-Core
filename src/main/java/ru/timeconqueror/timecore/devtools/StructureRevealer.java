package ru.timeconqueror.timecore.devtools;

import com.google.common.collect.ArrayListMultimap;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.timecore.api.util.RandHelper;
import ru.timeconqueror.timecore.mod.common.config.MainConfig;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.mod.common.packet.StructureRevealingS2CPacket;
import ru.timeconqueror.timecore.util.NetworkUtils;
import ru.timeconqueror.timecore.util.client.DrawHelper;
import ru.timeconqueror.timecore.util.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.util.reflection.UnlockedMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class StructureRevealer {
    private static final Lazy<StructureRevealer> INSTANCE = Lazy.of(() -> MainConfig.INSTANCE.areDevFeaturesEnabled() ? new StructureRevealer() : null);
    private static final UnlockedMethod<List<StructureStart>> M_GET_STARTS = ReflectionHelper.findObfMethod(Structure.class, "func_202371_a"/*getStarts*/, IWorld.class, int.class, int.class);

    /**
     * Structure renderer. Can be accessible only on client side. Will be null on dedicated server.
     */
    public final Renderer structureRenderer;
    private final ArrayListMultimap<Structure<?>, UUID> subscribedStructures = ArrayListMultimap.create();

    public StructureRevealer() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            structureRenderer = new Renderer();
            MinecraftForge.EVENT_BUS.addListener(structureRenderer::onWorldRender);//TODO move to FMLJavaModLoadingContext.get().getModEventBus()
            MinecraftForge.EVENT_BUS.addListener(structureRenderer::onChunkUnload);//TODO move to FMLJavaModLoadingContext.get().getModEventBus()
        } else {
            structureRenderer = null;
        }
    }

    @SubscribeEvent
    public static void onChunkLoadStatic(ChunkEvent.Load event) {
        if (getInstance() != null && event.getWorld() != null && !event.getWorld().isRemote()) {
            getInstance().onServerChunkLoad(event);
        }
    }

    public void onServerChunkLoad(ChunkEvent.Load event) {
        if (MainConfig.INSTANCE.areDevFeaturesEnabled()) {
            IWorld world = event.getWorld();
            ChunkPos pos = event.getChunk().getPos();

            for (Structure<?> structure : subscribedStructures.keySet()) {
                List<UUID> players = subscribedStructures.get(structure);

                if (!players.isEmpty()) {
                    List<StructureStart> starts = M_GET_STARTS.invoke(structure, world, pos.x, pos.z);

                    for (StructureStart start : starts) {
                        synchronized (start.getComponents()) {
                            for (StructurePiece component : start.getComponents()) {
                                AxisAlignedBB boundingBox = AxisAlignedBB.func_216363_a(component.getBoundingBox());
                                for (UUID uuid : players) {
                                    NetworkUtils.getPlayer(uuid).ifPresent(player ->
                                            InternalPacketManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new StructureRevealingS2CPacket(boundingBox, structure.getRegistryName())));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void subscribePlayerToStructure(ServerPlayerEntity player, Structure<?> structure) {
        if (!subscribedStructures.containsEntry(structure, player.getUniqueID())) {
            subscribedStructures.put(structure, player.getUniqueID());
        }
    }

    public void unsubscribePlayerFromStructure(ServerPlayerEntity player, Structure<?> structure) {
        subscribedStructures.remove(structure, player.getUniqueID());
    }

    public void unsubscribePlayerFromAllStructures(ServerPlayerEntity player) {
        for (Structure<?> structure : subscribedStructures.keySet()) {
            List<UUID> subscribedPlayers = subscribedStructures.get(structure);
            subscribedPlayers.removeIf(uuid -> uuid.equals(player.getUniqueID()));
        }
    }

    public List<ResourceLocation> getSubscriptions(ServerPlayerEntity player) {
        List<ResourceLocation> structures = new ArrayList<>();

        for (Structure<?> structure : subscribedStructures.keySet()) {
            List<UUID> subscribedPlayers = subscribedStructures.get(structure);
            if (subscribedPlayers.contains(player.getUniqueID())) {
                structures.add(structure.getRegistryName());
            }
        }

        return structures;
    }

    @Nullable
    public static StructureRevealer getInstance() {
        return INSTANCE.get();
    }

    public static class Renderer {
        private final List<StructurePieceContainer> trackedStructurePieces = new ArrayList<>();
        private final HashMap<ResourceLocation, Integer> structureColorMap = new HashMap<>();
        private boolean visibleThroughBlocks = false;

        public void onWorldRender(RenderWorldLastEvent event) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

            ActiveRenderInfo activeRenderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
            Vec3d projectedView = activeRenderInfo.getProjectedView();

            for (StructurePieceContainer container : trackedStructurePieces) {
                DrawHelper.buildFilledBoundingBox(buffer, container.getBb().offset(-projectedView.x, -projectedView.y, -projectedView.z), getStructureColor(container.getStructureName()));
            }

            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
            GlStateManager.disableTexture();

            if (visibleThroughBlocks) {
                GlStateManager.disableDepthTest();
            } else {
                GlStateManager.depthMask(false);
                GlStateManager.enablePolygonOffset();
                GlStateManager.polygonOffset(-3.0F, -3.0F);
            }

            tessellator.draw();

            if (visibleThroughBlocks) {
                GlStateManager.enableDepthTest();
            } else {
                GlStateManager.depthMask(true);
                GlStateManager.disablePolygonOffset();
            }

            GlStateManager.enableTexture();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
        }

        public void onChunkUnload(ChunkEvent.Unload event) {
            if (event.getWorld().isRemote()) {
                ChunkPos chunkPos = event.getChunk().getPos();
                Vec3d chunkStart = new Vec3d(chunkPos.getXStart(), 0, chunkPos.getZStart());
                Vec3d chunkEnd = new Vec3d(chunkPos.getXEnd(), 255, chunkPos.getZEnd());
                trackedStructurePieces.removeIf(trackedStructurePiece -> trackedStructurePiece.getBb().intersects(chunkStart, chunkEnd));
            }
        }

        public void trackStructurePiece(ResourceLocation structureName, AxisAlignedBB bb) {
            for (StructurePieceContainer container : trackedStructurePieces) {
                if (container.getBb().equals(bb)) {
                    return;
                }
            }

            trackedStructurePieces.add(new StructurePieceContainer(structureName, bb));
        }

        public void setVisibleThroughBlocks(boolean visibleThroughBlocks) {
            this.visibleThroughBlocks = visibleThroughBlocks;
        }

        public void setStructureColor(ResourceLocation structureName, int color) {
            structureColorMap.put(structureName, DrawHelper.opaquefy(color));
        }

        private int getStructureColor(ResourceLocation structureName) {
            return structureColorMap.computeIfAbsent(structureName, resourceLocation -> DrawHelper.opaquefy(RandHelper.RAND.nextInt()));
        }
    }
}
