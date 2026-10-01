package com.shashi.smartpantry.ui;

import android.os.Bundle;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.shashi.smartpantry.R;

/** Settings screen: two switches persisted in SharedPreferences. */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_settings);

        SwitchMaterial expirySwitch = findViewById(R.id.switch_expiry);
        SwitchMaterial almostSwitch = findViewById(R.id.switch_almost);

        expirySwitch.setChecked(AppSettings.expiryAlerts(this));
        almostSwitch.setChecked(AppSettings.showAlmostThere(this));

        expirySwitch.setOnCheckedChangeListener((b, checked) -> AppSettings.setExpiryAlerts(this, checked));
        almostSwitch.setOnCheckedChangeListener((b, checked) -> AppSettings.setShowAlmostThere(this, checked));

        setupBottomNav(R.id.nav_settings);
    }
}
