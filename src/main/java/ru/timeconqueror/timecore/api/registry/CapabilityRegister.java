package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import ru.timeconqueror.timecore.api.util.Temporal;

import java.util.ArrayList;
import java.util.List;

//ToDo javadoc
public class CapabilityRegister extends TimeRegister {
    private final Temporal<List<Class<?>>> capClasses = Temporal.of(new ArrayList<>());

    public CapabilityRegister(String modId) {
        super(modId);
    }

    public <T> Capability<T> register(Class<T> type) {
        capClasses.get().add(type);

        return CapabilityManager.get(new CapabilityToken<>() {
        });
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
}
