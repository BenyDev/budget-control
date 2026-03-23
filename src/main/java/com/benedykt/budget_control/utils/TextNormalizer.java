package com.benedykt.budget_control.utils;

public class TextNormalizer {
    public static String normalizeSpaces(String input) {
        return input.replaceAll("\\s+"," ").trim();
    }
}
