package ru.timeconqueror.timecore.registry.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.common.base.TimeRegistry;


/**
 * Used for simplifying block adding. You need to extend it and do your stuff in {@link #register()} method<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link TimeAutoRegistrable} with {@link TimeAutoRegistrable.Target#INSTANCE} target
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 * Examples can be seen at test module.
 */
public abstract class AdvancementCriterionTimeRegistry extends TimeRegistry {
    public static <I extends ICriterionInstance, T extends ICriterionTrigger<I>> T registerCriterionTrigger(T criterion) {
        return CriteriaTriggers.register(criterion);
    }

    @Override
    protected void register() {

    }
}
