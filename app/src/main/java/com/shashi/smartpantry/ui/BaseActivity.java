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

    private BottomNavigationView nav;
    private int ownItemId;

    protected void setupBottomNav(final int selectedItemId) {
        ownItemId = selectedItemId;
        nav = findViewById(R.id.bottom_nav);
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
            // false = keep THIS screen's own tab highlighted (the other screen highlights itself)
            return false;
        });
    }

    /**
     * A reused screen can come back to the front still showing the tab that was tapped
     * the last time it was visible, so always re-highlight this screen's own tab.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (nav != null && nav.getSelectedItemId() != ownItemId) {
            nav.getMenu().findItem(ownItemId).setChecked(true);
        }
    }
}
