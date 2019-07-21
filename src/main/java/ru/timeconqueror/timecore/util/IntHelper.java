package ru.timeconqueror.timecore.util;

public class IntHelper {
    public static boolean canBeParsed(String pValue) {
        try {
            Integer.parseInt(pValue);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
