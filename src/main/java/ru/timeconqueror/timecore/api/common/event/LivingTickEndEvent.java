package ru.timeconqueror.timecore.api.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/**
 * Called at the end of {@link LivingEntity#tick()}.
 * It exists, because {@link LivingTickEvent} is posted at the tick start, where {@code prevPos} is the same as {@code prevPos}.
 * <p>
 * This event is not {@link Cancelable}.<br>
 * <p>
 * This event does not have a result. {@link HasResult}<br>
 * <p>
 * Posted by {@link Mod.EventBusSubscriber.Bus#FORGE} event bus.
 */
public class LivingTickEndEvent extends LivingEvent {
    private final LogicalSide dist;

    public LivingTickEndEvent(LivingEntity entity) {
        super(entity);

        dist = entity.level().isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    public LogicalSide getLogicalSide() {
        return dist;
    }
}
