package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import ru.timeconqueror.timecore.api.util.Temporal;

import java.util.ArrayList;
import java.util.List;

public class CapabilityRegister extends TimeRegister {
    private final Temporal<List<Class<?>>> capClasses = Temporal.of(new ArrayList<>());

    public CapabilityRegister(String modId) {
        super(modId);
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(this::onEvent);
    }

    private void onEvent(RegisterCapabilitiesEvent event) {
        catchErrors(RegisterCapabilitiesEvent.class, () -> registerCaps(event));
    }

    private void registerCaps(RegisterCapabilitiesEvent event) {
        capClasses.doAndRemove(classes -> {
            for (Class<?> clazz : classes) {
                event.register(clazz);
            }
        });
    }

    public <T> void register(Class<T> type) {
        capClasses.get().add(type);
    }
}
