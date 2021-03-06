package ru.timeconqueror.timecore.devtools;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ChunkManagerHooks;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import ru.timeconqueror.timecore.mod.common.config.MainConfig;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.mod.common.packet.S2CSRClearPiecesPacket;
import ru.timeconqueror.timecore.mod.common.packet.S2CSRSendSinglePiecePacket;

import java.util.*;
import java.util.stream.Stream;

//ToDo unsubscribe show only sunscribed
public class StructureRevealer {
    private static final StructureRevealer INSTANCE = MainConfig.INSTANCE.devFeaturesEnabled.get() ? new StructureRevealer() : null;

    /**
     * Structure renderer. Can be accessible only on client side. Will be null on dedicated server.
     */
    public final StructureRenderer structureRenderer;
    private Multimap<UUID, Structure<?>> subscribedStructures = ArrayListMultimap.create();
    private final Multimap<UUID, ChunkPos> recentlyWatchedChunks = ArrayListMultimap.create();

    public StructureRevealer() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            structureRenderer = new StructureRenderer();
            MinecraftForge.EVENT_BUS.addListener(structureRenderer::onWorldRender);
            MinecraftForge.EVENT_BUS.addListener(structureRenderer::onClientLogin);
        } else {
            structureRenderer = null;
        }

        MinecraftForge.EVENT_BUS.addListener(this::onChunkWatch);
        MinecraftForge.EVENT_BUS.addListener(this::onTickStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
    }

    public static Optional<StructureRevealer> getInstance() {
        return Optional.ofNullable(INSTANCE);
    }

    public void subscribePlayerToStructure(ServerPlayerEntity player, Structure<?> structure) {
        if (!subscribedStructures.containsEntry(player.getUUID(), structure)) {
            subscribedStructures.put(player.getUUID(), structure);
            refreshAndSave(player);
        }
    }

    public void unsubscribePlayerFromStructure(ServerPlayerEntity player, Structure<?> structure) {
        subscribedStructures.remove(player.getUUID(), structure);
        refreshAndSave(player);
    }

    public void unsubscribePlayerFromAllStructures(ServerPlayerEntity player) {
        Collection<Structure<?>> structures = subscribedStructures.get(player.getUUID());
        if (structures != null) {
            structures.clear();

            refreshAndSave(player);
        }
    }

    public List<ResourceLocation> getSubscriptions(ServerPlayerEntity player) {
        List<ResourceLocation> locations = new ArrayList<>();

        subscribedStructures.get(player.getUUID()).forEach(structure -> locations.add(structure.getRegistryName()));

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
        ChunkManager chunkManager = playerIn.getLevel().getChunkSource().chunkMap;

        InternalPacketManager.sendToPlayer(playerIn, new S2CSRClearPiecesPacket());

        ChunkManagerHooks.getLoadedChunksIterable(chunkManager).forEach(chunkHolder -> {

            if (chunkManager.getPlayers(chunkHolder.getPos(), false)
                    .anyMatch(player -> player.getUUID().equals(playerIn.getUUID()))) {

                getSubscribedStructuresInChunk(playerIn.getLevel(), playerIn, chunkHolder.getPos()).forEach(data -> {
                    InternalPacketManager.sendToPlayer(playerIn, new S2CSRSendSinglePiecePacket(data));
                });
            }
        });
    }

    private void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerWorld world = event.getWorld();
        if (world != null && !world.isClientSide() && MainConfig.INSTANCE.devFeaturesEnabled.get()) {

            ServerPlayerEntity player = event.getPlayer();
            ChunkPos pos = event.getPos();

            recentlyWatchedChunks.put(player.getUUID(), pos);
        }
    }

    private void onTickStart(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            recentlyWatchedChunks.keySet().forEach(uuid -> {
                ServerPlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);

                if (player != null) {
                    recentlyWatchedChunks.get(uuid)
                            .forEach(chunkPos -> getSubscribedStructuresInChunk(player.getLevel(), player, chunkPos)
                                    .forEach(data -> InternalPacketManager.sendToPlayer(player, new S2CSRSendSinglePiecePacket(data))));
                }
            });
            recentlyWatchedChunks.clear();
        }
    }

    private List<StructureData> getSubscribedStructuresInChunk(ServerWorld world, ServerPlayerEntity player, ChunkPos pos) {
        List<StructureData> subscribedStructuresInChunk = new ArrayList<>();

        Collection<Structure<?>> structures = subscribedStructures.get(player.getUUID());

        for (Structure<?> structure : structures) {
            Stream<? extends StructureStart<?>> starts = world.startsForFeature(SectionPos.of(pos, 0), structure);

            starts.forEach(start -> {
                synchronized (start.getPieces()) {
                    for (StructurePiece component : start.getPieces()) {
                        AxisAlignedBB boundingBox = AxisAlignedBB.of(component.getBoundingBox());
                        subscribedStructuresInChunk.add(new StructureData(boundingBox, structure, world));
                    }
                }
            });
        }

        return subscribedStructuresInChunk;
    }

}
