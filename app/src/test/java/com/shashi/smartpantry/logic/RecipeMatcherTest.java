package com.shashi.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.shashi.smartpantry.model.PantryItem;
import com.shashi.smartpantry.model.Recipe;
import com.shashi.smartpantry.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Local JVM tests for the strict-matching rule (no emulator needed). Run with: ./gradlew test */
public class RecipeMatcherTest {

    private static Recipe fiveIngredientRecipe() {
        Recipe r = new Recipe(1, "Tomato Pasta", "steps");
        r.getIngredients().add(new RecipeIngredient("pasta", 200, "g"));
        r.getIngredients().add(new RecipeIngredient("tomato", 2, "pcs"));
        r.getIngredients().add(new RecipeIngredient("onion", 1, "pcs"));
        r.getIngredients().add(new RecipeIngredient("garlic", 2, "pcs"));
        r.getIngredients().add(new RecipeIngredient("olive oil", 2, "tbsp"));
        return r;
    }

    private static PantryItem p(String name, double qty, String unit) {
        return new PantryItem(0, name, qty, unit, "");
    }

    private static List<PantryItem> fourOfFive() {
        return new ArrayList<>(Arrays.asList(
                p("Pasta", 500, "g"), p("Tomatoes", 4, "pcs"), p("Onion", 3, "pcs"), p("Garlic", 5, "pcs")));
    }

    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        List<Recipe> result = RecipeMatcher.strictMatches(
                Collections.singletonList(fiveIngredientRecipe()), fourOfFive());
        assertTrue(result.isEmpty());
    }

    @Test
    public void allFiveIngredients_isSuggested_evenWithUnitDifference() {
        List<PantryItem> pantry = fourOfFive();
        pantry.add(p("olive oil", 100, "ml")); // recipe asks for tbsp, pantry has ml
        List<Recipe> result = RecipeMatcher.strictMatches(
                Collections.singletonList(fiveIngredientRecipe()), pantry);
        assertEquals(1, result.size());
    }

    @Test
    public void insufficientQuantity_isNotSuggested() {
        List<PantryItem> pantry = fourOfFive();
        pantry.set(0, p("Pasta", 150, "g")); // needs 200 g
        pantry.add(p("olive oil", 100, "ml"));
        assertTrue(RecipeMatcher.strictMatches(
                Collections.singletonList(fiveIngredientRecipe()), pantry).isEmpty());
    }

    @Test
    public void oneMissing_appearsOnlyInAlmostThere() {
        List<Recipe> recipes = Collections.singletonList(fiveIngredientRecipe());
        assertEquals(1, RecipeMatcher.almostThere(recipes, fourOfFive()).size());
        assertTrue(RecipeMatcher.strictMatches(recipes, fourOfFive()).isEmpty());
    }

    @Test
    public void emptyPantry_suggestsNothing() {
        assertTrue(RecipeMatcher.strictMatches(
                Collections.singletonList(fiveIngredientRecipe()), new ArrayList<PantryItem>()).isEmpty());
    }

    @Test
    public void pluralAndCaseDifferences_areIgnored() {
        assertEquals("tomato", IngredientMatcher.normaliseName("Tomatoes"));
        assertEquals("egg", IngredientMatcher.normaliseName("  EGGS "));
        assertEquals("berry", IngredientMatcher.normaliseName("berries"));
        assertEquals("asparagus", IngredientMatcher.normaliseName("asparagus"));
    }

    @Test
    public void unitConversion_withinSameFamily() {
        assertTrue(IngredientMatcher.hasEnough(1, "kg", 500, "g"));
        assertFalse(IngredientMatcher.hasEnough(400, "g", 0.5, "kg"));
        assertFalse(IngredientMatcher.hasEnough(500, "g", 100, "ml")); // different families
    }

    @Test
    public void duplicatePantryRows_areCombined() {
        List<PantryItem> pantry = fourOfFive();
        pantry.set(0, p("pasta", 100, "g"));
        pantry.add(p("Pasta", 0.1, "kg"));
        pantry.add(p("olive oil", 100, "ml"));
        assertEquals(1, RecipeMatcher.strictMatches(
                Collections.singletonList(fiveIngredientRecipe()), pantry).size());
    }
}
