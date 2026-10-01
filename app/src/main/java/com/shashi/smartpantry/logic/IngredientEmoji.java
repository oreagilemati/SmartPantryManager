package com.shashi.smartpantry.logic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Picks a small emoji for an ingredient so the pantry list feels friendlier.
 * Names are normalised first (so "Tomatoes" finds "tomato"), then matched
 * exactly, then by a keyword inside the name ("cherry tomato" finds "tomato").
 * Unknown ingredients get a plate. Pure Java, so it is unit tested on a JVM.
 */
public final class IngredientEmoji {

    private static final int DEFAULT_CODE_POINT = 0x1F37D; // plate with cutlery

    /** Insertion order matters: longer, more specific names come before short ones. */
    private static final Map<String, Integer> CODE_POINTS = new LinkedHashMap<>();

    static {
        add("tomato", 0x1F345);
        add("egg", 0x1F95A);
        add("rice", 0x1F35A);
        add("pasta", 0x1F35D);
        add("macaroni", 0x1F35D);
        add("noodle", 0x1F35C);
        add("onion", 0x1F9C5);
        add("garlic", 0x1F9C4);
        add("potato", 0x1F954);
        add("carrot", 0x1F955);
        add("bell pepper", 0x1F336);
        add("curry powder", 0x1F336);
        add("cheese", 0x1F9C0);
        add("butter", 0x1F9C8);
        add("milk", 0x1F95B);
        add("bread", 0x1F35E);
        add("flour", 0x1F33E);
        add("maize meal", 0x1F33D);
        add("sugar", 0x1F36C);
        add("salt", 0x1F9C2);
        add("honey", 0x1F36F);
        add("banana", 0x1F34C);
        add("avocado", 0x1F951);
        add("lemon", 0x1F34B);
        add("chicken", 0x1F357);
        add("tuna", 0x1F41F);
        add("oat", 0x1F963);
        add("lentil", 0x1F372);
        add("baked bean", 0x1F96B);
        add("soy sauce", 0x1F962);
        add("olive oil", 0x1F33F);
        add("vegetable oil", 0x1F33B);
        add("spring onion", 0x1F331);
        add("stock", 0x1F372);
        add("apple", 0x1F34E);
        add("orange", 0x1F34A);
        add("mushroom", 0x1F344);
        add("corn", 0x1F33D);
        add("fish", 0x1F41F);
        add("beef", 0x1F356);
        add("pepper", 0x1F336);
    }

    private static void add(String name, int codePoint) {
        CODE_POINTS.put(name, codePoint);
    }

    private IngredientEmoji() { }

    public static String forName(String rawName) {
        String name = IngredientMatcher.normaliseName(rawName);
        Integer exact = CODE_POINTS.get(name);
        if (exact != null) return toText(exact);
        for (Map.Entry<String, Integer> e : CODE_POINTS.entrySet()) {
            if (name.contains(e.getKey())) return toText(e.getValue());
        }
        return toText(DEFAULT_CODE_POINT);
    }

    private static String toText(int codePoint) {
        return new String(Character.toChars(codePoint));
    }
}
