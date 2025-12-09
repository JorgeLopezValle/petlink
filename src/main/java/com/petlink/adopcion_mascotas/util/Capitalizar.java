package com.petlink.adopcion_mascotas.util;

public class Capitalizar {

    public static String capitalizar(String input) {

        String stringLimpio = input.trim();

        if (stringLimpio.isEmpty()) {
            return "";
        }

        return stringLimpio.substring(0, 1).toUpperCase() +
                stringLimpio.substring(1).toLowerCase();
    }
}