package com.shashi.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shashi.smartpantry.model.PantryItem;
import com.shashi.smartpantry.model.Recipe;
import com.shashi.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Local SQLite database for the Smart Pantry Manager.
 *
 * Tables:
 *   pantry_items        - the user's ingredients (full CRUD)
 *   recipes             - seeded on first run (read only)
 *   recipe_ingredients  - the ingredient lines of each recipe (read only)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // pantry_items
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_P_ID = "_id";
    public static final String COL_P_NAME = "name";
    public static final String COL_P_QTY = "quantity";
    public static final String COL_P_UNIT = "unit";
    public static final String COL_P_EXPIRY = "expiry_date";

    // recipes
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_R_ID = "_id";
    public static final String COL_R_NAME = "name";
    public static final String COL_R_STEPS = "steps";

    // recipe_ingredients
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "_id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QTY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // so deleting a recipe removes its ingredient rows
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_P_NAME + " TEXT NOT NULL, "
                + COL_P_QTY + " REAL NOT NULL, "
                + COL_P_UNIT + " TEXT NOT NULL, "
                + COL_P_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_R_NAME + " TEXT NOT NULL, "
                + COL_R_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RI_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_RI_NAME + " TEXT NOT NULL, "
                + COL_RI_QTY + " REAL NOT NULL, "
                + COL_RI_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES "
                + TABLE_RECIPES + "(" + COL_R_ID + ") ON DELETE CASCADE)");

        // seed the 20 recipes (runs once, inside onCreate's transaction)
        SeedData.insertAll(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple strategy for this assignment: rebuild everything.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ------------------------------------------------------------------ CREATE
    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_PANTRY, null, toValues(item));
    }

    // -------------------------------------------------------------------- READ
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_P_NAME + " COLLATE NOCASE ASC");
        try {
            while (c.moveToNext()) items.add(fromCursor(c));
        } finally {
            c.close();
        }
        return items;
    }

    /** Returns null if no row has this id. */
    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, COL_P_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            return c.moveToFirst() ? fromCursor(c) : null;
        } finally {
            c.close();
        }
    }

    // ------------------------------------------------------------------ UPDATE
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_PANTRY, toValues(item), COL_P_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    // ------------------------------------------------------------------ DELETE
    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_P_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ----------------------------------------------------------------- RECIPES
    /** Loads every recipe together with its ingredient lines (2 queries, no N+1 problem). */
    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();
        Map<Long, Recipe> byId = new HashMap<>();
        List<Recipe> recipes = new ArrayList<>();

        Cursor rc = db.query(TABLE_RECIPES, null, null, null, null, null, COL_R_NAME + " ASC");
        try {
            while (rc.moveToNext()) {
                Recipe r = new Recipe(rc.getLong(rc.getColumnIndexOrThrow(COL_R_ID)),
                        rc.getString(rc.getColumnIndexOrThrow(COL_R_NAME)),
                        rc.getString(rc.getColumnIndexOrThrow(COL_R_STEPS)));
                byId.put(r.getId(), r);
                recipes.add(r);
            }
        } finally {
            rc.close();
        }

        Cursor ic = db.query(TABLE_RECIPE_INGREDIENTS, null, null, null, null, null, COL_RI_ID + " ASC");
        try {
            while (ic.moveToNext()) {
                Recipe owner = byId.get(ic.getLong(ic.getColumnIndexOrThrow(COL_RI_RECIPE_ID)));
                if (owner != null) {
                    owner.getIngredients().add(new RecipeIngredient(
                            ic.getString(ic.getColumnIndexOrThrow(COL_RI_NAME)),
                            ic.getDouble(ic.getColumnIndexOrThrow(COL_RI_QTY)),
                            ic.getString(ic.getColumnIndexOrThrow(COL_RI_UNIT))));
                }
            }
        } finally {
            ic.close();
        }
        return recipes;
    }

    /** Returns null if no recipe has this id. */
    public Recipe getRecipe(long id) {
        for (Recipe r : getAllRecipes()) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    // ----------------------------------------------------------------- HELPERS
    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put(COL_P_NAME, item.getName());
        v.put(COL_P_QTY, item.getQuantity());
        v.put(COL_P_UNIT, item.getUnit());
        v.put(COL_P_EXPIRY, item.getExpiryDate());
        return v;
    }

    private PantryItem fromCursor(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(COL_P_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_P_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(COL_P_QTY)),
                c.getString(c.getColumnIndexOrThrow(COL_P_UNIT)),
                c.getString(c.getColumnIndexOrThrow(COL_P_EXPIRY)));
    }
}
