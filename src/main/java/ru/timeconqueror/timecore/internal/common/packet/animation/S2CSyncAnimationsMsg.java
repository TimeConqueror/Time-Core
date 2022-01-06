package ru.timeconqueror.timecore.internal.common.packet.animation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.ServerAnimationManager;
import ru.timeconqueror.timecore.animation.util.AnimationSerializer;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.common.packet.ITimePacketHandler;

import java.util.Map;

public class S2CSyncAnimationsMsg {
	//server side only
	private ServerAnimationManager<?> serverAnimationManager;
	// client side only
	private Map<String, AnimationWatcher> layerMap;
	private final int entityId;

	public S2CSyncAnimationsMsg(ServerAnimationManager<?> animationManager, Entity entity) {
		this.serverAnimationManager = animationManager;
		this.entityId = entity.getId();
	}

	private S2CSyncAnimationsMsg(Map<String, AnimationWatcher> layerMap, int entityId) {
		this.layerMap = layerMap;
		this.entityId = entityId;
	}

	public static class Handler implements ITimePacketHandler<S2CSyncAnimationsMsg> {

		@Override
		public void encode(S2CSyncAnimationsMsg packet, FriendlyByteBuf buffer) {
			buffer.writeInt(packet.entityId);
			AnimationSerializer.serializeWatchers(packet.serverAnimationManager, buffer);
		}

		@NotNull
		@Override
		public S2CSyncAnimationsMsg decode(FriendlyByteBuf buffer) {
			int entityId = buffer.readInt();
			Map<String, AnimationWatcher> layerMap = AnimationSerializer.deserializeWatchers(buffer);

			return new S2CSyncAnimationsMsg(layerMap, entityId);
		}

		@Override
		public boolean handle(S2CSyncAnimationsMsg packet, NetworkEvent.Context ctx) {
			ctx.enqueueWork(() -> {
				String errorMessage = null;

				Entity entity = getWorld(ctx).getEntity(packet.entityId);
				if (entity == null) {
					errorMessage = "Client received an animation, but entity wasn't found on client.";
				} else if (!(entity instanceof AnimatedObject<?>)) {
					errorMessage = "Provided entity id belongs to entity, which is not an inheritor of " + AnimatedObject.class;
				}

				if (errorMessage == null) {
					Map<String, AnimationWatcher> layerMap = packet.layerMap;
					BaseAnimationManager animationManager = (BaseAnimationManager) ((AnimatedObject<?>) entity).getActionManager().getAnimationManager();

					layerMap.forEach((name, watcher) -> animationManager.getLayer(name).setAnimationWatcher(watcher));
				} else {
					TimeCore.LOGGER.error(errorMessage);
				}
			});

			return true;
		}
	}
}
