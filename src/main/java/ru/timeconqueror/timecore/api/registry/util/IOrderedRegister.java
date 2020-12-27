package ru.timeconqueror.timecore.api.registry.util;

import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import ru.timeconqueror.timecore.api.registry.TimeRegister;

import java.util.List;

/**
 * All {@link IModBusEvent} events, to which {@link TimeRegister}s are subscribed,
 * will be loaded after the same events in TimeRegisters from {@link #getDependencies()}.
 */
public interface IOrderedRegister {
    List<Class<? extends TimeRegister>> getDependencies();
}
