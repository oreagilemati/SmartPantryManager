package com.shashi.smartpantry.ui;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shashi.smartpantry.R;

/**
 * Shared behaviour for the three top-level screens (Pantry, Suggested, Settings):
 * wires the bottom navigation bar to explicit Intents.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected void setupBottomNav(final int selectedItemId) {
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(selectedItemId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedItemId) return true;

            Intent intent;
            if (id == R.id.nav_pantry) {
                intent = new Intent(this, MainActivity.class);
            } else if (id == R.id.nav_suggested) {
                intent = new Intent(this, SuggestedRecipesActivity.class);
            } else {
                intent = new Intent(this, SettingsActivity.class);
            }
            // Reuse an existing instance instead of stacking a new copy every tap
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        });
    }
}
