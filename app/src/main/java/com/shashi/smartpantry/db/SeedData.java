package com.shashi.smartpantry.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.shashi.smartpantry.model.Recipe;
import com.shashi.smartpantry.model.RecipeIngredient;

/** Pre-loads the recipe collection the first time the database is created. */
final class SeedData {

    private SeedData() { }

    static void insertAll(SQLiteDatabase db) {
        for (Recipe r : RecipeCatalog.all()) {
            ContentValues rv = new ContentValues();
            rv.put(DatabaseHelper.COL_R_NAME, r.getName());
            rv.put(DatabaseHelper.COL_R_STEPS, r.getSteps());
            long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, rv);

            for (RecipeIngredient ing : r.getIngredients()) {
                ContentValues iv = new ContentValues();
                iv.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
                iv.put(DatabaseHelper.COL_RI_NAME, ing.getName());
                iv.put(DatabaseHelper.COL_RI_QTY, ing.getQuantity());
                iv.put(DatabaseHelper.COL_RI_UNIT, ing.getUnit());
                db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, iv);
            }
        }
    }
}
