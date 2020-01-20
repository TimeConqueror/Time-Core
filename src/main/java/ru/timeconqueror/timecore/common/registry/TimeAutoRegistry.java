package ru.timeconqueror.timecore.common.registry;

import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be used on your registry class which inherits {@link ForgeTimeRegistry}
 * to make it visible for auto-registry system.
 * <p>
 * <b><font color="yellow">WARNING: Annotated registry class must contain constructor without params or exception will be thrown.</b>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeAutoRegistry {
    Type ASM_TYPE = Type.getType(TimeAutoRegistry.class);
}
