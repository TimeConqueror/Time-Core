package ru.timeconqueror.timecore.molang;

import gg.moonflower.molangcompiler.api.MolangCompiler;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import ru.timeconqueror.molang.custom.QueryDomain;
import ru.timeconqueror.timecore.api.Markers;
import ru.timeconqueror.timecore.api.molang.MolangQueryDomain;
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper;
import ru.timeconqueror.timecore.util.AnnoScanningHelper;

import java.util.Arrays;

@Log4j2
public class MolangLoader {
    private static final Type QUERY_DOMAIN_TYPE = Type.getType(MolangQueryDomain.class);

    public static MolangCompiler load(ClassLoader classLoader) {
        return MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, classLoader);
    }

    public static void handleQueryDomainAnnotations(ModFileScanData scanResults) {
        AnnoScanningHelper.allDataForType(scanResults.getAnnotations(), QUERY_DOMAIN_TYPE)
                .forEach(data -> {
                    Class<?> clazz = AnnoScanningHelper.getClass(data);
                    Arrays.stream(clazz.getDeclaredFields())
                            .filter(ReflectionHelper::isStatic)
                            .filter(ReflectionHelper::isPublic)
                            .filter(ReflectionHelper::isFinal)
                            .filter(field -> field.getType().equals(String.class))
                            .forEach(field -> {
                                String domain = AnnoScanningHelper.getData(data, "value");

                                String query;
                                try {
                                    query = (String) field.get(null);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }

                                registerRedirection(query, domain);
                            });
                });
    }

    private static synchronized void registerRedirection(String query, String domain) {
        log.debug(Markers.ANIMATIONS, "Registered redirect for query '{}' to domain '{}'", query, domain);
        QueryDomain.register(query, domain);
    }
}
