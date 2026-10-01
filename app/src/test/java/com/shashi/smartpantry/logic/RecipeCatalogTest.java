package com.shashi.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.shashi.smartpantry.db.RecipeCatalog;
import com.shashi.smartpantry.model.PantryItem;
import com.shashi.smartpantry.model.Recipe;
import com.shashi.smartpantry.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Checks the built-in recipes and the emoji helper. Run with: ./gradlew testDebugUnitTest */
public class RecipeCatalogTest {

    private static final Set<String> ALLOWED_UNITS =
            new HashSet<>(Arrays.asList("g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"));

    private static Recipe find(String name) {
        for (Recipe r : RecipeCatalog.all()) if (r.getName().equals(name)) return r;
        throw new AssertionError("recipe not found: " + name);
    }

    private static PantryItem p(String name, double qty, String unit) {
        return new PantryItem(0, name, qty, unit, "");
    }

    @Test
    public void catalogHasTwentyTwoRecipes_withUniqueNames() {
        List<Recipe> all = RecipeCatalog.all();
        assertEquals(22, all.size());
        Set<String> names = new HashSet<>();
        for (Recipe r : all) names.add(r.getName());
        assertEquals(22, names.size());
    }

    @Test
    public void everyIngredient_isValid() {
        for (Recipe r : RecipeCatalog.all()) {
            assertFalse(r.getName(), r.getSteps().trim().isEmpty());
            assertTrue(r.getName(), r.getIngredients().size() >= 3);
            for (RecipeIngredient i : r.getIngredients()) {
                assertFalse(r.getName(), i.getName().trim().isEmpty());
                assertTrue(r.getName() + " / " + i.getName(), i.getQuantity() > 0);
                assertTrue(r.getName() + " / " + i.getUnit(),
                        ALLOWED_UNITS.contains(IngredientMatcher.canonicalUnit(i.getUnit())));
            }
        }
    }

    @Test
    public void papAndChakalaka_needsEveryIngredient() {
        Recipe pap = find("Pap and Chakalaka");
        List<PantryItem> pantry = new ArrayList<>(Arrays.asList(
                p("Maize Meal", 1, "kg"), p("Onions", 2, "pcs"), p("Tomatoes", 3, "pcs"),
                p("Carrots", 2, "pcs"), p("Baked beans", 2, "pcs"), p("Curry powder", 1, "tbsp")));
        // vegetable oil missing -> not suggested
        assertTrue(RecipeMatcher.strictMatches(Arrays.asList(pap), pantry).isEmpty());
        assertEquals(Arrays.asList("vegetable oil"), RecipeMatcher.missingIngredients(pap, pantry));
        pantry.add(p("Vegetable oil", 500, "ml"));
        assertEquals(1, RecipeMatcher.strictMatches(Arrays.asList(pap), pantry).size());
    }

    @Test
    public void chickenBunnyChow_notSuggestedWithoutEnoughChicken() {
        Recipe bunny = find("Chicken Bunny Chow");
        List<PantryItem> pantry = new ArrayList<>(Arrays.asList(
                p("Chicken breast", 250, "g"), p("Onion", 1, "pcs"), p("Garlic", 3, "pcs"),
                p("Tomato", 2, "pcs"), p("Curry powder", 30, "ml"), p("Vegetable oil", 1, "tbsp"),
                p("Bread", 8, "pcs")));
        assertTrue(RecipeMatcher.strictMatches(Arrays.asList(bunny), pantry).isEmpty()); // 250 g < 300 g
        pantry.set(0, p("Chicken breast", 0.5, "kg"));
        assertEquals(1, RecipeMatcher.strictMatches(Arrays.asList(bunny), pantry).size());
    }

    @Test
    public void emoji_matchesExactAndByKeyword() {
        String tomato = new String(Character.toChars(0x1F345));
        assertEquals(tomato, IngredientEmoji.forName("Tomatoes"));
        assertEquals(tomato, IngredientEmoji.forName("cherry tomato"));
        assertEquals(new String(Character.toChars(0x1F33D)), IngredientEmoji.forName("Maize meal"));
        assertEquals(new String(Character.toChars(0x1F357)), IngredientEmoji.forName("chicken breast"));
        assertEquals(new String(Character.toChars(0x1F37D)), IngredientEmoji.forName("dragon fruit")); // default plate
        assertEquals(new String(Character.toChars(0x1F37D)), IngredientEmoji.forName(null));
    }

    @Test
    public void everySeededIngredient_hasASpecificEmoji() {
        String plate = new String(Character.toChars(0x1F37D));
        for (Recipe r : RecipeCatalog.all())
            for (RecipeIngredient i : r.getIngredients())
                assertFalse(r.getName() + " / " + i.getName(), plate.equals(IngredientEmoji.forName(i.getName())));
    }
}
