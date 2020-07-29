package ru.timeconqueror.timecore.devtools;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ChunkManagerHooks;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import ru.timeconqueror.timecore.mod.common.config.MainConfig;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.mod.common.packet.S2CSRClearPiecesMsg;
import ru.timeconqueror.timecore.mod.common.packet.S2CSRSendSinglePieceMsg;
import ru.timeconqueror.timecore.mod.mixins.accessor.StructureAccessor;

import java.util.*;

public class StructureRevealer {
    private static final StructureRevealer INSTANCE = MainConfig.INSTANCE.areDevFeaturesEnabled() ? new StructureRevealer() : null;

    /**
     * Structure renderer. Can be accessible only on client side. Will be null on dedicated server.
     */
    public final StructureRenderer structureRenderer;
    private Multimap<UUID, Structure<?>> subscribedStructures = ArrayListMultimap.create();

    public StructureRevealer() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            structureRenderer = new StructureRenderer();
            MinecraftForge.EVENT_BUS.addListener(structureRenderer::onWorldRender);
            MinecraftForge.EVENT_BUS.addListener(structureRenderer::onClientLogin);
        } else {
            structureRenderer = null;
        }

        MinecraftForge.EVENT_BUS.addListener(this::onChunkWatch);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
    }

    public static Optional<StructureRevealer> getInstance() {
        return Optional.ofNullable(INSTANCE);
    }

    public void subscribePlayerToStructure(ServerPlayerEntity player, Structure<?> structure) {
        if (!subscribedStructures.containsEntry(player.getUniqueID(), structure)) {
            subscribedStructures.put(player.getUniqueID(), structure);
            refreshAndSave(player);
        }
    }

    public void unsubscribePlayerFromStructure(ServerPlayerEntity player, Structure<?> structure) {
        subscribedStructures.remove(player.getUniqueID(), structure);
        refreshAndSave(player);
    }

    public void unsubscribePlayerFromAllStructures(ServerPlayerEntity player) {
        Collection<Structure<?>> structures = subscribedStructures.get(player.getUniqueID());
        if (structures != null) {
            structures.clear();

            refreshAndSave(player);
        }
    }

    public List<ResourceLocation> getSubscriptions(ServerPlayerEntity player) {
        List<ResourceLocation> locations = new ArrayList<>();

        subscribedStructures.get(player.getUniqueID()).forEach(structure -> locations.add(structure.getRegistryName()));

        return locations;
    }

    private void onServerStart(FMLServerStartingEvent event) {
        subscribedStructures = new RevealerDataSaver().restoreOnServer();
    }

    private void refreshAndSave(ServerPlayerEntity player) {
        refreshAllStructureData(player);
        new RevealerDataSaver().saveOnServer(subscribedStructures);
    }

    private void refreshAllStructureData(ServerPlayerEntity playerIn) {
        ChunkManager chunkManager = playerIn.getServerWorld().getChunkProvider().chunkManager;

        InternalPacketManager.sendToPlayer(playerIn, new S2CSRClearPiecesMsg());

        ChunkManagerHooks.getLoadedChunksIterable(chunkManager).forEach(chunkHolder -> {

            if (chunkManager.getTrackingPlayers(chunkHolder.getPosition(), false)
                    .anyMatch(player -> player.getUniqueID().equals(playerIn.getUniqueID()))) {

                getSubscribedStructuresInChunk(playerIn.world, playerIn, chunkHolder.getPosition()).forEach(data -> {
                    InternalPacketManager.sendToPlayer(playerIn, new S2CSRSendSinglePieceMsg(data));
                });
            }
        });
    }

    private void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerWorld world = event.getWorld();
        if (world != null && !world.isRemote() && MainConfig.INSTANCE.areDevFeaturesEnabled()) {
            ServerPlayerEntity player = event.getPlayer();
            ChunkPos pos = event.getPos();

            getSubscribedStructuresInChunk(world, player, pos)
                    .forEach(data -> InternalPacketManager.sendToPlayer(player, new S2CSRSendSinglePieceMsg(data)));
        }
    }

    private List<StructureData> getSubscribedStructuresInChunk(World world, ServerPlayerEntity player, ChunkPos pos) {
        List<StructureData> subscribedStructuresInChunk = new ArrayList<>();

        DimensionType dim = world.getDimension().getType();
        Collection<Structure<?>> structures = subscribedStructures.get(player.getUniqueID());

        for (Structure<?> structure : structures) {
            List<StructureStart> starts = ((StructureAccessor) structure).getStarts(world, pos.x, pos.z);

            for (StructureStart start : starts) {
                synchronized (start.getComponents()) {
                    for (StructurePiece component : start.getComponents()) {
                        AxisAlignedBB boundingBox = AxisAlignedBB.toImmutable(component.getBoundingBox());
                        subscribedStructuresInChunk.add(new StructureData(boundingBox, structure, dim));
                    }
                }
            }
        }

        return subscribedStructuresInChunk;
    }

}
