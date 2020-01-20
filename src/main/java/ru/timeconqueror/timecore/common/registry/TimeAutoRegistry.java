package ru.timeconqueror.timecore.common.registry;

import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO get rid of it
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeAutoRegistry {
    Type ASM_TYPE = Type.getType(TimeAutoRegistry.class);
}
