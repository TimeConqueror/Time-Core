package ru.timeconqueror.timecore.util;

import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.objectweb.asm.Type;

import java.util.Set;
import java.util.stream.Stream;

public class AnnoScanningHelper {
    public static Class<?> getClass(AnnotationData data) {
        String className = data.clazz().getClassName();
        try {
            return Class.forName(className);
        } catch (Throwable e) {
            throw new RuntimeException(String.format("There was an exception while trying to load %s", className), e);
        }
    }

    public static Stream<AnnotationData> allDataForType(Set<AnnotationData> annotations, Type type) {
        return annotations.stream().filter(data -> data.annotationType().equals(type));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getData(AnnotationData data, String key) {
        return (T) data.annotationData().get(key);
    }
}
