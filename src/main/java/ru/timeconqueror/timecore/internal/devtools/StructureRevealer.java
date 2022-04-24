package ru.timeconqueror.timecore.internal.devtools;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.timeconqueror.timecore.internal.common.config.MainConfig;
import ru.timeconqueror.timecore.internal.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.internal.common.packet.S2CSRClearPiecesPacket;
import ru.timeconqueror.timecore.internal.common.packet.S2CSRSendSinglePiecePacket;
import ru.timeconqueror.timecore.mixins.accessor.ChunkMapAccessor;

import java.util.*;

//ToDo unsubscribe show only sunscribed
public class StructureRevealer {
    private static final StructureRevealer INSTANCE = MainConfig.INSTANCE.devFeaturesEnabled.get() ? new StructureRevealer() : null;

    /**
     * Structure renderer. Can be accessible only on client side. Will be null on dedicated server.
     */
    public final StructureRenderer structureRenderer;
    private Multimap<UUID, StructureFeature<?>> subscribedStructures = ArrayListMultimap.create();
    private final Multimap<UUID, ChunkPos> recentlyWatchedChunks = ArrayListMultimap.create();

    public StructureRevealer() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            structureRenderer = new StructureRenderer();
//            MinecraftForge.EVENT_BUS.addListener(structureRenderer::onWorldRender);//FIXME PORT
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

    public void subscribePlayerToStructure(ServerPlayer player, StructureFeature<?> structure) {
        if (!subscribedStructures.containsEntry(player.getUUID(), structure)) {
            subscribedStructures.put(player.getUUID(), structure);
            refreshAndSave(player);
        }
    }

    public void unsubscribePlayerFromStructure(ServerPlayer player, StructureFeature<?> structure) {
        subscribedStructures.remove(player.getUUID(), structure);
        refreshAndSave(player);
    }

    public void unsubscribePlayerFromAllStructures(ServerPlayer player) {
        Collection<StructureFeature<?>> structures = subscribedStructures.get(player.getUUID());
        if (structures != null) {
            structures.clear();

            refreshAndSave(player);
        }
    }

    public List<ResourceLocation> getSubscriptions(ServerPlayer player) {
        List<ResourceLocation> locations = new ArrayList<>();

        subscribedStructures.get(player.getUUID()).forEach(structure -> locations.add(structure.getRegistryName()));

        return locations;
    }

    private void onServerStart(ServerStartingEvent event) {
        subscribedStructures = new RevealerDataSaver().restoreOnServer();
    }

    private void refreshAndSave(ServerPlayer player) {
        refreshAllStructureData(player);
        new RevealerDataSaver().saveOnServer(subscribedStructures);
    }

    private void refreshAllStructureData(ServerPlayer playerIn) {
        ChunkMap chunkManager = playerIn.getLevel().getChunkSource().chunkMap;

        InternalPacketManager.sendToPlayer(playerIn, new S2CSRClearPiecesPacket());

        ((ChunkMapAccessor) chunkManager).callGetChunks().forEach(chunkHolder -> {
            if (chunkManager.getPlayers(chunkHolder.getPos(), false).stream()
                    .anyMatch(player -> player.getUUID().equals(playerIn.getUUID()))) {

                getSubscribedStructuresInChunk(playerIn.getLevel(), playerIn, chunkHolder.getPos()).forEach(data -> {
                    InternalPacketManager.sendToPlayer(playerIn, new S2CSRSendSinglePiecePacket(data));
                });
            }
        });
    }

    private void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerLevel world = event.getWorld();
        if (world != null && !world.isClientSide() && MainConfig.INSTANCE.devFeaturesEnabled.get()) {

            ServerPlayer player = event.getPlayer();
            ChunkPos pos = event.getPos();

            recentlyWatchedChunks.put(player.getUUID(), pos);
        }
    }

    private void onTickStart(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            recentlyWatchedChunks.keySet().forEach(uuid -> {
                ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);

                if (player != null) {
                    recentlyWatchedChunks.get(uuid)
                            .forEach(chunkPos -> getSubscribedStructuresInChunk(player.getLevel(), player, chunkPos)
                                    .forEach(data -> InternalPacketManager.sendToPlayer(player, new S2CSRSendSinglePiecePacket(data))));
                }
            });
            recentlyWatchedChunks.clear();
        }
    }

    private List<StructureData> getSubscribedStructuresInChunk(ServerLevel world, ServerPlayer player, ChunkPos pos) {
        List<StructureData> subscribedStructuresInChunk = new ArrayList<>();

        Collection<StructureFeature<?>> structures = subscribedStructures.get(player.getUUID());

        for (StructureFeature<?> structure : structures) {
            //TODO FIXME
//            List<? extends StructureStart<?>> starts = world.startsForFeature(SectionPos.of(pos, 0), structure);
//
//            starts.forEach(start -> {
//                synchronized (start.getPieces()) {
//                    for (StructurePiece component : start.getPieces()) {
//                        AABB boundingBox = AABB.of(component.getBoundingBox());
//                        subscribedStructuresInChunk.add(new StructureData(boundingBox, structure, world));
//                    }
//                }
//            });
        }

        return subscribedStructuresInChunk;
    }

}
