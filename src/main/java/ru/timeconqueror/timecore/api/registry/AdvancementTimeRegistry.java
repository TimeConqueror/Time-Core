package ru.timeconqueror.timecore.api.registry;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Used for simplifying advancement elements adding. You may extend it and do all your stuff inside
 * or just call static methods from elsewhere.<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link TimeAutoRegistrable}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 */
public abstract class AdvancementTimeRegistry implements Initable {
    public static <I extends ICriterionInstance, T extends ICriterionTrigger<I>> T registerCriterionTrigger(T criterion) {
        return CriteriaTriggers.register(criterion);
    }

    @Override
    public final void onInit(FMLCommonSetupEvent event) {
    }
}
