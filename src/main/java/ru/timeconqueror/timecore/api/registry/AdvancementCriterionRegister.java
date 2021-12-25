package ru.timeconqueror.timecore.api.registry;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.api.util.Temporal;

import java.util.ArrayList;
import java.util.List;

public class AdvancementCriterionRegister extends TimeRegister {
    private final Temporal<List<CriterionTrigger<?>>> criteria = Temporal.of(new ArrayList<>());

    public AdvancementCriterionRegister(String modid) {
        super(modid);
    }

    public <I extends CriterionTriggerInstance, T extends CriterionTrigger<I>> T register(T criterion) {
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
