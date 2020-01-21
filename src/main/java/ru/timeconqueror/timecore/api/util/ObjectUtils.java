package ru.timeconqueror.timecore.api.util;

import org.jetbrains.annotations.Contract;
import ru.timeconqueror.timecore.TimeCore;

public class ObjectUtils {
    @Contract("null,_ -> false")
    public static boolean checkIfNotNull(Object object, String errorMessage) {
        if (object == null) {
            TimeCore.LOGGER.error(errorMessage, new NullPointerException());
            return false;
        }

        return true;
    }
}
