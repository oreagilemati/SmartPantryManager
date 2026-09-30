package com.shashi.smartpantry.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** Pre-loads 20 recipes the first time the database is created. */
final class SeedData {

    private SeedData() { }

    /** Each recipe: name, steps, then ingredients as {name, quantity, unit}. */
    private static final Object[][] RECIPES = {
        {"Tomato Pasta", "1. Boil the pasta in salted water until tender.\n2. Fry chopped garlic in olive oil for 1 minute.\n3. Add chopped tomatoes and simmer for 10 minutes.\n4. Toss the drained pasta through the sauce and serve.",
            new Object[][]{{"pasta", 200, "g"}, {"tomato", 4, "pcs"}, {"garlic", 2, "pcs"}, {"olive oil", 2, "tbsp"}, {"salt", 1, "tsp"}}},
        {"Egg Fried Rice", "1. Cook the rice and let it cool.\n2. Scramble the eggs in hot oil and set aside.\n3. Fry chopped onion, then add the rice.\n4. Stir in soy sauce and the eggs. Serve hot.",
            new Object[][]{{"rice", 200, "g"}, {"egg", 2, "pcs"}, {"onion", 1, "pcs"}, {"soy sauce", 2, "tbsp"}, {"vegetable oil", 1, "tbsp"}}},
        {"Cheese Omelette", "1. Beat the eggs with salt.\n2. Melt the butter in a pan over medium heat.\n3. Pour in the eggs and cook until nearly set.\n4. Add grated cheese, fold and serve.",
            new Object[][]{{"egg", 3, "pcs"}, {"cheese", 50, "g"}, {"butter", 1, "tbsp"}, {"salt", 1, "tsp"}}},
        {"Fluffy Pancakes", "1. Whisk flour, sugar, egg and milk into a smooth batter.\n2. Melt a little butter in a pan.\n3. Pour in small rounds of batter and cook until bubbles form.\n4. Flip and cook until golden.",
            new Object[][]{{"flour", 200, "g"}, {"egg", 1, "pcs"}, {"milk", 300, "ml"}, {"sugar", 2, "tbsp"}, {"butter", 1, "tbsp"}}},
        {"Scrambled Eggs on Toast", "1. Toast the bread.\n2. Melt butter in a pan and add the beaten eggs with salt.\n3. Stir gently until softly set.\n4. Spoon over the toast.",
            new Object[][]{{"egg", 3, "pcs"}, {"bread", 2, "pcs"}, {"butter", 1, "tbsp"}, {"salt", 1, "tsp"}}},
        {"Garlic Bread", "1. Mash softened butter with crushed garlic.\n2. Spread over the bread slices.\n3. Bake at 200 C for 8-10 minutes until crisp.",
            new Object[][]{{"bread", 4, "pcs"}, {"butter", 50, "g"}, {"garlic", 2, "pcs"}}},
        {"Potato Soup", "1. Fry the chopped onion in butter until soft.\n2. Add diced potatoes and stock.\n3. Simmer for 20 minutes until the potatoes are soft.\n4. Mash or blend, season with salt and serve.",
            new Object[][]{{"potato", 4, "pcs"}, {"onion", 1, "pcs"}, {"vegetable stock", 500, "ml"}, {"butter", 1, "tbsp"}, {"salt", 1, "tsp"}}},
        {"Creamy Mashed Potato", "1. Peel and boil the potatoes until soft.\n2. Drain and mash with butter.\n3. Stir in warm milk and salt until smooth.",
            new Object[][]{{"potato", 5, "pcs"}, {"butter", 50, "g"}, {"milk", 100, "ml"}, {"salt", 1, "tsp"}}},
        {"Veggie Stir-fry", "1. Slice the carrots, pepper and onion.\n2. Heat oil in a wok and fry the garlic.\n3. Add the vegetables and stir-fry for 5 minutes.\n4. Add soy sauce, toss and serve.",
            new Object[][]{{"carrot", 2, "pcs"}, {"bell pepper", 1, "pcs"}, {"onion", 1, "pcs"}, {"garlic", 2, "pcs"}, {"soy sauce", 2, "tbsp"}, {"vegetable oil", 1, "tbsp"}}},
        {"Cheese Toastie", "1. Butter the outside of the bread.\n2. Fill with cheese.\n3. Grill in a pan for 3 minutes per side until golden and melted.",
            new Object[][]{{"bread", 2, "pcs"}, {"cheese", 60, "g"}, {"butter", 1, "tbsp"}}},
        {"Banana Oat Porridge", "1. Heat the milk and oats together, stirring, for 5 minutes.\n2. Slice in the banana.\n3. Drizzle with honey and serve.",
            new Object[][]{{"oats", 60, "g"}, {"banana", 1, "pcs"}, {"milk", 250, "ml"}, {"honey", 1, "tbsp"}}},
        {"Tuna Pasta", "1. Boil the pasta.\n2. Fry the chopped onion in olive oil.\n3. Stir in the tuna and salt.\n4. Toss with the pasta and serve.",
            new Object[][]{{"pasta", 200, "g"}, {"tuna", 1, "pcs"}, {"onion", 1, "pcs"}, {"olive oil", 1, "tbsp"}, {"salt", 1, "tsp"}}},
        {"Lentil Curry", "1. Fry onion and garlic in oil.\n2. Add curry powder and chopped tomatoes.\n3. Add rinsed lentils and 600 ml water.\n4. Simmer for 25 minutes until thick.",
            new Object[][]{{"lentil", 200, "g"}, {"onion", 1, "pcs"}, {"tomato", 2, "pcs"}, {"garlic", 2, "pcs"}, {"curry powder", 2, "tsp"}, {"vegetable oil", 1, "tbsp"}}},
        {"Egg Fried Noodles", "1. Cook the noodles and drain.\n2. Scramble the eggs in hot oil.\n3. Add the noodles, soy sauce and sliced spring onion.\n4. Toss for 2 minutes and serve.",
            new Object[][]{{"noodle", 200, "g"}, {"egg", 2, "pcs"}, {"soy sauce", 2, "tbsp"}, {"vegetable oil", 1, "tbsp"}, {"spring onion", 2, "pcs"}}},
        {"Chicken Rice Bowl", "1. Season and fry the chicken pieces in oil until cooked.\n2. Fry onion and garlic.\n3. Serve over cooked rice.",
            new Object[][]{{"chicken breast", 300, "g"}, {"rice", 200, "g"}, {"onion", 1, "pcs"}, {"garlic", 2, "pcs"}, {"vegetable oil", 1, "tbsp"}, {"salt", 1, "tsp"}}},
        {"Beans on Toast", "1. Toast the bread and butter it.\n2. Warm the baked beans in a pan.\n3. Pour over the toast.",
            new Object[][]{{"baked bean", 1, "pcs"}, {"bread", 2, "pcs"}, {"butter", 1, "tbsp"}}},
        {"French Toast", "1. Whisk eggs, milk and sugar.\n2. Dip each slice of bread.\n3. Fry in butter until golden on both sides.",
            new Object[][]{{"bread", 4, "pcs"}, {"egg", 2, "pcs"}, {"milk", 100, "ml"}, {"sugar", 1, "tbsp"}, {"butter", 1, "tbsp"}}},
        {"Simple Tomato Soup", "1. Fry onion and garlic in olive oil.\n2. Add chopped tomatoes and stock.\n3. Simmer for 20 minutes, then blend until smooth.",
            new Object[][]{{"tomato", 6, "pcs"}, {"onion", 1, "pcs"}, {"garlic", 2, "pcs"}, {"vegetable stock", 500, "ml"}, {"olive oil", 1, "tbsp"}}},
        {"Macaroni Cheese", "1. Boil the macaroni.\n2. Melt butter, stir in flour, then whisk in milk to make a sauce.\n3. Melt in the cheese.\n4. Combine with the macaroni and serve.",
            new Object[][]{{"macaroni", 250, "g"}, {"cheese", 150, "g"}, {"milk", 300, "ml"}, {"butter", 2, "tbsp"}, {"flour", 2, "tbsp"}}},
        {"Quick Guacamole", "1. Mash the avocados.\n2. Stir in chopped tomato and onion.\n3. Squeeze in the lemon juice, add salt and serve.",
            new Object[][]{{"avocado", 2, "pcs"}, {"lemon", 1, "pcs"}, {"tomato", 1, "pcs"}, {"onion", 1, "pcs"}, {"salt", 1, "tsp"}}},
    };

    static void insertAll(SQLiteDatabase db) {
        for (Object[] r : RECIPES) {
            ContentValues rv = new ContentValues();
            rv.put(DatabaseHelper.COL_R_NAME, (String) r[0]);
            rv.put(DatabaseHelper.COL_R_STEPS, (String) r[1]);
            long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, rv);

            for (Object[] ing : (Object[][]) r[2]) {
                ContentValues iv = new ContentValues();
                iv.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
                iv.put(DatabaseHelper.COL_RI_NAME, (String) ing[0]);
                iv.put(DatabaseHelper.COL_RI_QTY, ((Number) ing[1]).doubleValue());
                iv.put(DatabaseHelper.COL_RI_UNIT, (String) ing[2]);
                db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, iv);
            }
        }
    }
}
