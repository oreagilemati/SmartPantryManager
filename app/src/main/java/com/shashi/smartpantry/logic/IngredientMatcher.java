package com.shashi.smartpantry.logic;

import java.util.Locale;

/**
 * Pure-Java helper (no Android imports) that makes ingredient names and
 * quantities comparable. Keeping it free of Android classes means it can be
 * unit tested on a plain JVM.
 */
public final class IngredientMatcher {

    private IngredientMatcher() { }

    /** Lower-cases, trims, collapses spaces and reduces a name to a singular form. */
    public static String normaliseName(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (s.isEmpty()) return "";
        String[] words = s.split(" ");
        // only the last word is plural: cherry tomatoes -> cherry tomato
        words[words.length - 1] = singular(words[words.length - 1]);
        return String.join(" ", words);
    }

    /** Small singularising rule set - enough for typical pantry items. */
    static String singular(String w) {
        int n = w.length();
        if (n <= 3) return w;
        if (w.equals("asparagus") || w.equals("hummus") || w.equals("couscous")
                || w.equals("molasses") || w.equals("watercress")) return w;
        if (w.equals("leaves")) return "leaf";
        if (w.equals("loaves")) return "loaf";
        if (w.endsWith("ies") && n > 4) return w.substring(0, n - 3) + "y"; // berries
        if (w.endsWith("oes")) return w.substring(0, n - 2);                // tomatoes
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("sses")
                || w.endsWith("xes")) return w.substring(0, n - 2);         // peaches
        if (w.endsWith("ss") || w.endsWith("us")) return w;                 // cress
        if (w.endsWith("s")) return w.substring(0, n - 1);                  // eggs
        return w;
    }

    /** Maps a unit string to a canonical unit (g, kg, ml, l, tsp, tbsp, cup, pcs). */
    public static String canonicalUnit(String raw) {
        if (raw == null) return "pcs";
        String u = raw.trim().toLowerCase(Locale.ROOT).replace(".", "");
        switch (u) {
            case "g": case "gram": case "grams": case "gm": return "g";
            case "kg": case "kilogram": case "kilograms": case "kgs": return "kg";
            case "ml": case "millilitre": case "milliliter": case "millilitres": case "milliliters": return "ml";
            case "l": case "litre": case "liter": case "litres": case "liters": return "l";
            case "tsp": case "teaspoon": case "teaspoons": return "tsp";
            case "tbsp": case "tablespoon": case "tablespoons": return "tbsp";
            case "cup": case "cups": return "cup";
            case "pc": case "pcs": case "piece": case "pieces": case "unit": case "units":
            case "whole": case "each": case "": return "pcs";
            default: return u;
        }
    }

    /** Unit family: "mass", "volume", "count", or the raw unit when unknown. */
    public static String family(String unit) {
        switch (canonicalUnit(unit)) {
            case "g": case "kg": return "mass";
            case "ml": case "l": case "tsp": case "tbsp": case "cup": return "volume";
            case "pcs": return "count";
            default: return canonicalUnit(unit);
        }
    }

    /** Converts a quantity to its family's base unit: grams, millilitres or pieces. */
    public static double toBase(double qty, String unit) {
        switch (canonicalUnit(unit)) {
            case "kg": return qty * 1000.0;
            case "l": return qty * 1000.0;
            case "tsp": return qty * 5.0;     // 1 tsp  ~ 5 ml
            case "tbsp": return qty * 15.0;   // 1 tbsp ~ 15 ml
            case "cup": return qty * 250.0;   // 1 cup  ~ 250 ml
            default: return qty;
        }
    }

    /**
     * True when the pantry amount covers the required amount. Units in the same
     * family are converted (kg vs g, tbsp vs ml). Units from different families
     * cannot be compared, so they are NOT treated as available - the strict rule
     * always errs on the safe side.
     */
    public static boolean hasEnough(double haveQty, String haveUnit, double needQty, String needUnit) {
        if (!family(haveUnit).equals(family(needUnit))) return false;
        return toBase(haveQty, haveUnit) + 1e-9 >= toBase(needQty, needUnit);
    }
}
