package ru.timeconqueror.timecore.api.registry.util;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import ru.timeconqueror.timecore.api.registry.TimeRegister;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers the instance from the field to the mod bus, by calling {@link TimeRegister#regToBus(IEventBus)}
 * Can be applied to static fields with type, which inherits {@link TimeRegister}.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRegistrable {

    /**
     * Registers method to the {@link FMLConstructModEvent}.
     * Can be applied to static methods with zero parameters or with one parameter with type {@link FMLConstructModEvent}.
     * <p>
     * If method has zero parameters, it will be called slightly before {@link FMLConstructModEvent}.
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface InitMethod {
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Entries {
        String value();

        String registryKey();
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {
    }
}
