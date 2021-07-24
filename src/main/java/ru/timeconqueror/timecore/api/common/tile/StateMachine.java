//package ru.timeconqueror.timecore.api.common.tile;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.IFormattableTextComponent;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraft.world.World;
//import net.minecraft.world.chunk.Chunk;
//import net.minecraft.world.server.ServerChunkProvider;
//import net.minecraftforge.fml.network.PacketDistributor;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.message.Message;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import ru.timeconqueror.timecore.api.util.Auxiliary;
//import ru.timeconqueror.timecore.api.util.NetworkUtils;
//import ru.timeconqueror.timecore.api.util.Pair;
//
//import javax.annotation.OverridingMethodsMustInvokeSuper;
//import java.util.Objects;
//
//public abstract class StateMachine<STATE extends StateMachine.State> {
//    private static final Logger LOGGER = LogManager.getLogger();
//
//    @Nullable
//    private Pair<State, State> pendingStateUpdate = null;
//    private STATE state;
//    protected StateBasedTile owner;
//    /**
//     * Determines if tile entity is placed, but was never read from nbt.
//     */
//    private boolean justPlaced = true;
//
//    /**
//     * Method where you can init anything, that requires world and {@link #owner} to be not null.
//     */
//    @OverridingMethodsMustInvokeSuper
//    public void onLoad() {
//        if (justPlaced) {
//            justPlaced = false;
//            onPlace();
//        }
//    }
//
//    /**
//     * Called for both sides when tile entity is just placed in world and has never been write to and read from nbt.
//     */
//    public void onPlace() {
//    }
//
//    @OverridingMethodsMustInvokeSuper
//    public void onTick() {
//        if (isServerSide()) {
//            if (pendingStateUpdate != null) {
//                if (pendingStateUpdate.right() == getState()) { //if it's still the same
//                    sendUpdatePacketToNearby(new SPDelayedChangeStage(this, pendingStateUpdate.left()));
//                }
//
//                pendingStateUpdate = null;
//            }
//        }
//
//        if (getState() != null) {
//            getState().onTick();
//        }
//    }
//
//    public void sendTo(PlayerEntity player, IFormattableTextComponent component) {
//        NetworkUtils.sendMessage(player, component);
//    }
//
//    public void sendTo(PlayerEntity player, IFormattableTextComponent component, TextFormatting format) {
//        sendTo(player, component.withStyle(format));
//    }
//
//    /**
//     * Saves current data to the disk and sends update to client.
//     */
//    public void saveAndSync() {
//        if (isServerSide()) {
//            owner.saveAndSync();
//        }
//    }
//
//    /**
//     * Saves current data to the disk without sending update to client.
//     */
//    public void save() {
//        if (isServerSide()) {
//            owner.save();
//        }
//    }
//
//    @OverridingMethodsMustInvokeSuper
//    public void writeNBT(CompoundNBT nbt, SerializationType type) {
//        serializeStage(this, nbt, type);
//        LOGGER.debug(DEBUG_MARKER, formatLogMessage("state '{}' was serialized for {}."), getState(), type == SerializationType.SAVE ? "saving" : "syncing");
//    }
//
//    @OverridingMethodsMustInvokeSuper
//    public void readNBT(CompoundNBT nbt, SerializationType type) {
//        setState(deserializeStage(this, nbt, type));
//        LOGGER.debug(DEBUG_MARKER, formatLogMessage("stage '{}' was deserialized {}."), getState(), type == SerializationType.SAVE ? "from saved file" : "on client");
//
//        justPlaced = false;
//
//        onStateStart(type == SerializationType.SYNC);
//    }
//
//    /**
//     * Sends update packet to the client with given {@link CompoundNBT} to all players, tracking the game.
//     */
//    public void sendUpdatePacketToNearby(IServerGamePacket packet) {
//        if (!isServerSide()) {
//            return;
//        }
//
//        Chunk chunk = getWorld().getChunkAt(getPos());
//        LGNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new SPacketGameUpdate(this, packet));
//
//        LOGGER.debug(DEBUG_MARKER, () -> logMessage("update packet '{}' was sent.", packet.getClass().getSimpleName()));
//    }
//
//    public void sendUpdatePacketToNearbyExcept(ServerPlayerEntity excepting, IServerGamePacket packet) {
//        if (!isServerSide()) {
//            return;
//        }
//
//        Chunk chunk = getWorld().getChunkAt(getPos());
//        ((ServerChunkProvider) chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false) // copied line from PacketDistributor#trackingChunk
//                .filter(player -> !player.getUUID().equals(excepting.getUUID()))
//                .forEach(player -> LGNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SPacketGameUpdate(this, packet)));
//
//        LOGGER.debug(DEBUG_MARKER, () -> logMessage("update packet '{}' to all tracking except {} was sent.", packet.getClass().getSimpleName(), excepting.getName()));
//    }
//
//    /**
//     * Fired on client when {@link IServerGamePacket} comes from server.
//     */
//    public void onUpdatePacket(IServerGamePacket packet) {
//        packet.runOnClient(this);
//    }
//
//    /**
//     * Sends update packet to the server with given {@link CompoundNBT}.
//     */
//    public void sendFeedbackPacket(IClientGamePacket packet) {
//        if (isServerSide()) {
//            return;
//        }
//
//        LGNetwork.INSTANCE.sendToServer(new CPacketGameUpdate(this, packet));
//        LOGGER.debug(DEBUG_MARKER, () -> logMessage("feedback packet '{}' was sent.", packet.getClass().getSimpleName()));
//    }
//
//    /**
//     * Fired on server when {@link IClientGamePacket} comes from client.
//     */
//    public void onFeedbackPacket(ServerPlayerEntity sender, IClientGamePacket packet) {
//        packet.runOnServer(sender, this);
//    }
//
//    /**
//     * Only for usage from {@link #onPlace()}.
//     * Will be synced later.
//     *
//     * @param stage initial stage of game
//     */
//    public void setupInitialState(STATE stage) {
//        LOGGER.debug(DEBUG_MARKER, formatLogMessage("initial stage '{}' was set up."), stage);
//
//        setState(stage);
//        onStateUpdate(null, stage, true);
//        onStateStart(isClientSide());
//    }
//
//    public void switchState(@Nullable STATE stage) {
//        STATE old = this.getState();
//        if (old != null) old.onEnd();
//
//        LOGGER.debug(DEBUG_MARKER, formatLogMessage("switching from stage '{}' to '{}'."), old, stage);
//
//        setState(stage);
//        onStateUpdate(old, stage, false);
//        onStateStart(isClientSide());
//    }
//
//    /**
//     * Called for both logical sides when the game was switched to this stage.
//     * For server: called only when you manually change stage on server side via {@link #setupInitialState(State)} or {@link #switchState(State)}
//     * For client: called every time server sends new state, including deserializing from saved nbt.
//     */
//    protected void onStateUpdate(@Nullable STATE oldStage, @Nullable STATE newStage, boolean shouldDelayPacketSending) {
//        if (isServerSide()) {
//            if (newStage != null) newStage.preInit();
//            save();
//
//            if (shouldDelayPacketSending) {
//                pendingStageUpdate = Pair.of(oldStage, newStage);
//                LOGGER.debug(DEBUG_MARKER, () -> logMessage("update packet '{}' was delayed for sending till the next tick."));
//            } else {
//                sendUpdatePacketToNearby(new SPChangeStage(this));
//            }
//
//            if (newStage != null) newStage.postInit();
//        }
//    }
//
//    @Nullable
//    public abstract STATE createStateFromNBT(String id, CompoundNBT stateNBT, SerializationType serializationType);
//
//    @Nullable
//    public STATE getState() {
//        return state;
//    }
//
//    private void setState(@Nullable STATE state) {
//        this.state = state;
//    }
//
//    /**
//     * Called for both logical sides when the game was switched to this stage:
//     * <ol>- by changing stage via {@link #setupInitialState(State)} or {@link #switchState(State)}</ol>
//     * <ol>- by deserializing and syncing</ol>
//     * <p>
//     * Warning: {@link #getWorld()} can return null here, because world is set after reading from nbt!
//     */
//    protected void onStateStart(boolean clientSide) {
//        if (this.state != null) {
//            this.state.onStart(clientSide);
//        }
//    }
//
//    public boolean isServerSide() {
//        return !isClientSide();
//    }
//
//    public boolean isClientSide() {
//        return getWorld().isClientSide();
//    }
//
//    @NotNull
//    public World getWorld() {
//        return Objects.requireNonNull(owner.getLevel());
//    }
//
//    public BlockPos getPos() {
//        return owner.getBlockPos();
//    }
//
//    public static void serializeStage(StateMachine<?> machine, CompoundNBT nbt, SerializationType serializationType) {
//        State state = machine.getState();
//        if (state != null) {
//            CompoundNBT stateWrapper = new CompoundNBT();
//            stateWrapper.put("state", state.serialize(serializationType));
//            stateWrapper.putString("id", state.getID());
//            nbt.put("state_wrapper", stateWrapper);
//        }
//    }
//
//    @Nullable
//    public static <S extends State> S deserializeStage(StateMachine<S> machine, CompoundNBT nbt, SerializationType serializationType) {
//        if (nbt.contains("state_wrapper")) {
//            CompoundNBT stageWrapper = nbt.getCompound("state_wrapper");
//            return machine.createStateFromNBT(stageWrapper.getString("id"), stageWrapper.getCompound("state"), serializationType);
//        } else {
//            return null;
//        }
//    }
//
//    private Message logMessage(String message, Object... arguments) {
//        return Auxiliary.makeLogMessage(formatLogMessage(message), arguments);
//    }
//
//    private String formatLogMessage(String message) {
//        return TextFormatting.DARK_BLUE + getClass().getSimpleName() + ": " + message;
//    }
//
//    public abstract static class State {
//        /**
//         * Called for both logical sides when the game was switched to this stage:
//         * <ol>- by changing stage via {@link #setupInitialState(State)} or {@link #switchState(State)}</ol>
//         * <ol>- by deserializing and syncing</ol>
//         * <p>
//         * Warning: {@link #getWorld()} can return null here, because world is set after reading from nbt!
//         */
//        protected void onStart(boolean clientSide) {
//
//        }
//
//        /**
//         * Called on every tick for both logical sides.
//         */
//        protected void onTick() {
//
//        }
//
//        /**
//         * Called for both logical sides when the game was switched from this stage to another one.
//         */
//        protected void onEnd() {
//
//        }
//
//        /**
//         * Serializes stage according to the provided serialization type.
//         * If you have some sensitive data you can check here for type before adding it to nbt or not.
//         *
//         * @param serializationType defines for which purpose stage is serializing.
//         */
//        public CompoundNBT serialize(SerializationType serializationType) {
//            return new CompoundNBT();
//        }
//
//        public abstract String getID();
//
//        @Override
//        public String toString() {
//            return getID();
//        }
//
//        /**
//         * Called on server side right after the game switched to this stage {@link #setupInitialState(State)} or {@link #switchState(State)},
//         * but BEFORE it will be saved and synced.
//         * <p>
//         * Is not called upon serializing and deserializing.
//         */
//        public void preInit() {
//
//        }
//
//        /**
//         * Called on server side right after the game switched to this stage {@link #setupInitialState(State)} or {@link #switchState(State)} ,
//         * and AFTER it will be saved and synced.
//         * <p>
//         * Is not called upon serializing and deserializing.
//         */
//        public void postInit() {
//
//        }
//    }
//}
