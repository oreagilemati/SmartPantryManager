package com.shashi.smartpantry.logic;

import com.shashi.smartpantry.model.PantryItem;
import com.shashi.smartpantry.model.Recipe;
import com.shashi.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The core business logic of the app (assignment section 2.3).
 *
 * STRICT RULE: a recipe is suggested only if EVERY ingredient it requires is in
 * the pantry in at least the required quantity. One missing or insufficient
 * ingredient excludes the whole recipe.
 */
public final class RecipeMatcher {

    private RecipeMatcher() { }

    /** Returns only the recipes that can be cooked right now. */
    public static List<Recipe> strictMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) {
            if (missingIngredients(r, pantry).isEmpty()) result.add(r);
        }
        return result;
    }

    /** Optional stretch: recipes missing exactly ONE ingredient. Shown in a separate list. */
    public static List<Recipe> almostThere(List<Recipe> recipes, List<PantryItem> pantry) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) {
            if (missingIngredients(r, pantry).size() == 1) result.add(r);
        }
        return result;
    }

    /** Names (as written in the recipe) of the ingredients the pantry cannot cover. */
    public static List<String> missingIngredients(Recipe recipe, List<PantryItem> pantry) {
        // Group pantry rows by normalised name so "Tomato" and "tomatoes" are the same ingredient
        Map<String, List<PantryItem>> byName = new HashMap<>();
        for (PantryItem p : pantry) {
            String key = IngredientMatcher.normaliseName(p.getName());
            List<PantryItem> list = byName.get(key);
            if (list == null) {
                list = new ArrayList<>();
                byName.put(key, list);
            }
            list.add(p);
        }

        List<String> missing = new ArrayList<>();
        for (RecipeIngredient need : recipe.getIngredients()) {
            String key = IngredientMatcher.normaliseName(need.getName());
            if (!isCovered(need, byName.get(key))) missing.add(need.getName());
        }
        return missing;
    }

    /** True if the pantry rows (same ingredient, comparable units) add up to the required amount. */
    private static boolean isCovered(RecipeIngredient need, List<PantryItem> owned) {
        if (owned == null) return false;
        String needFamily = IngredientMatcher.family(need.getUnit());
        double haveBase = 0;
        for (PantryItem p : owned) {
            if (IngredientMatcher.family(p.getUnit()).equals(needFamily)) {
                haveBase += IngredientMatcher.toBase(p.getQuantity(), p.getUnit());
            }
        }
        double needBase = IngredientMatcher.toBase(need.getQuantity(), need.getUnit());
        return haveBase + 1e-9 >= needBase;
    }
}
