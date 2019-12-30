package ru.timeconqueror.timecore.api.auxiliary;

public class IntHelper {
    public static boolean isInt(String s) {
        if (s == null) {
            return false;
        }

        int result = 0;
        int i = 0, len = s.length();
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+')
                    return false;

                if (len == 1) // Cannot have lone "+" or "-"
                    return false;
                i++;
            }
            multmin = limit / 10;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++), 10);
                if (digit < 0) {
                    return false;
                }
                if (result < multmin) {
                    return false;
                }
                result *= 10;
                if (result < limit + digit) {
                    return false;
                }
                result -= digit;
            }
        } else {
            return false;
        }

        return true;
    }
}
