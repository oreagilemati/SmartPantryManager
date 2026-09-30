package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL, " +
                "unit TEXT, " +
                "expiryDate TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    public long addItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiryDate", expiryDate);
        return db.insert("pantry", null, values);
    }

    public List<PantryItem> getAllItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("pantry", null, null, null, null, null, "name ASC");
        while (cursor.moveToNext()) {
            items.add(new PantryItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                    cursor.getString(cursor.getColumnIndexOrThrow("expiryDate"))
            ));
        }
        cursor.close();
        return items;
    }

    public void updateItem(int id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiryDate", expiryDate);
        db.update("pantry", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }
}
