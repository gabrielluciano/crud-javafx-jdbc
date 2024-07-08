package com.gabrielluciano.crudjfxjdbc.util;

public class NumberUtils {

    public static Integer tryParseToInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
