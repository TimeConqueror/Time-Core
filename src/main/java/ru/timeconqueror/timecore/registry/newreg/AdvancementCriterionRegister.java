package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.util.Temporal;

import java.util.ArrayList;
import java.util.List;

public class AdvancementCriterionRegister extends TimeRegister {
    private final Temporal<List<ICriterionTrigger<?>>> criteria = Temporal.of(new ArrayList<>());

    public AdvancementCriterionRegister(String modid) {
        super(modid);
    }

    public <I extends ICriterionInstance, T extends ICriterionTrigger<I>> T register(T criterion) {
        criteria.get().add(criterion);
        return criterion;
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(this::onSetup);
    }

    private void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> criteria.transferAndRemove(triggers -> triggers.forEach(CriteriaTriggers::register)));
    }
}
