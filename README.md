# Smart Pantry Manager

A Java Android app that helps reduce food waste. The user records the ingredients they already have at home (their *pantry*), and the app suggests only the recipes they can cook **right now** from those ingredients. No shopping trip needed.

Built for **Mobile App Development 700** (Faculty of Information Technology, RGIT), Practical Assignment.

## Features

| Screen | What it does |
|---|---|
| **Pantry List** | Shows every ingredient (RecyclerView + custom adapter bound to SQLite). Edit / delete buttons on each row, floating **+** button to add. Expired / expiring-soon items are colour-highlighted. |
| **Add / Edit Ingredient** | Form with input validation (name, quantity, unit spinner, optional expiry via date picker). |
| **Suggested Recipes** | Runs the **strict-matching rule**: a recipe appears only if *every* ingredient is in the pantry in at least the required quantity. Shows a friendly message when nothing matches. |
| **Recipe Detail** | Full ingredient list (with a tick / cross per ingredient) and the method. |
| **Settings** | Toggle expiring-soon highlighting; toggle the optional separate **"Almost there"** list (recipes missing exactly one ingredient). |

* 22 recipes are pre-loaded into the database on first run, including two South African favourites: **Pap and Chakalaka** and **Chicken Bunny Chow**.
* Matching is robust to simple real-world messiness: `Tomato` / `tomatoes`, `Eggs` / `egg`, `1 kg` vs `500 g`, `tbsp` vs `ml`.
* Bottom navigation between the three main screens; explicit Intents (with extras) for Add/Edit and Recipe Detail.
* **No** maps, GPS, location or internet permissions. The app is fully offline.

## Design

A clean, modern **white and pink** look: white background, pink app bar and highlights, flat cards with a soft pink outline and rounded corners. Each ingredient shows a small emoji (a tomato for tomatoes, corn for maize meal and so on) and the on-screen wording is deliberately friendly and a little trendy. All colours live in `res/values/colors.xml`, so the whole look can be changed by editing a handful of hex values.

## Database choice: SQLite (`SQLiteOpenHelper`)

I chose local SQLite because:

1. The data is personal and small (a pantry list and a fixed recipe set), so no cloud sync is needed.
2. It works offline, so the app is fully usable in a kitchen with no signal.
3. It has no external services, accounts or API keys, so the project runs on any machine straight after cloning.
4. `SQLiteOpenHelper` is the approach taught in the module's persistent-data chapter and lets me demonstrate raw SQL (`CREATE TABLE`, foreign keys, `ON DELETE CASCADE`) directly.

Tables: `pantry_items`, `recipes`, `recipe_ingredients` (see `docs/er_diagram.png`). Pantry items support full CRUD and persist after the app is closed and reopened.

## Project structure

```
app/src/main/java/com/shashi/smartpantry/
  model/   PantryItem, Recipe, RecipeIngredient              (plain data classes)
  logic/   IngredientMatcher, RecipeMatcher, IngredientEmoji (strict-matching rule + emoji, no Android imports)
  db/      DatabaseHelper, SeedData, RecipeCatalog            (SQLite + 22 seeded recipes)
  ui/      MainActivity, AddEditIngredientActivity, SuggestedRecipesActivity,
           RecipeDetailActivity, SettingsActivity, BaseActivity,
           PantryAdapter, RecipeAdapter, AppSettings, Format
app/src/test/  RecipeMatcherTest, RecipeCatalogTest (JUnit, run without an emulator)
docs/          screen_flow.png, er_diagram.png
```

## How the strict-matching rule works

`RecipeMatcher.missingIngredients(recipe, pantry)` lists every recipe ingredient the pantry cannot cover. A recipe is *suggested* only when that list is **empty**. For each ingredient it:

1. normalises the name (lower-case, trim, singular form) and finds the matching pantry rows;
2. adds up those rows, converting units inside the same family (kg/g, l/ml/tsp/tbsp/cup, pieces);
3. checks the total is at least the required quantity. Units from different families (e.g. grams vs pieces) are treated as **not available**, so the rule never suggests a recipe by guessing.

## Setup and run

**Requirements:** Android Studio (Hedgehog or newer), JDK 17 (bundled with Android Studio), Android device or emulator running Android 7.0 (API 24) or higher.

1. Clone the repository:
   ```
   git clone <YOUR-REPO-URL>
   ```
2. In Android Studio choose **File > Open** and select the project folder. Wait for Gradle sync to finish.
3. Connect a phone with USB debugging on, or create an emulator (Device Manager > Create Device).
4. Press **Run** (green triangle). The database and the 22 recipes are created automatically on first launch.
5. To run the unit tests for the matching logic: `./gradlew test` (or right-click `RecipeMatcherTest` > Run).

## Try it

1. Open **Pantry** and add: Pasta 200 g, Tomatoes 2 pcs, Garlic 2 pcs, Salt 1 tsp. *(Tomato Pasta needs 4 tomatoes + 2 tbsp olive oil, so it must **not** appear yet.)*
2. Open **Recipes**: Tomato Pasta is absent; the "No recipes match" message shows if nothing else qualifies.
3. Edit Tomatoes to 4 pcs and add Olive oil 30 ml. Return to **Recipes**: Tomato Pasta now appears.
4. Delete Olive oil: Tomato Pasta disappears again.

## Author

Shashi. RGIT, Mobile App Development 700.
