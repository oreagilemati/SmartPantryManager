package com.shashi.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shashi.smartpantry.R;
import com.shashi.smartpantry.db.DatabaseHelper;
import com.shashi.smartpantry.model.PantryItem;

import java.util.Calendar;
import java.util.Locale;

/** Add / Edit Ingredient screen. Edit mode is triggered by passing EXTRA_ITEM_ID in the Intent. */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"};

    private TextInputLayout nameLayout, qtyLayout;
    private TextInputEditText nameInput, qtyInput, expiryInput;
    private Spinner unitSpinner;
    private DatabaseHelper db;
    private long editingId = -1; // -1 = creating a new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        nameLayout = findViewById(R.id.til_name);
        qtyLayout = findViewById(R.id.til_quantity);
        nameInput = findViewById(R.id.et_name);
        qtyInput = findViewById(R.id.et_quantity);
        expiryInput = findViewById(R.id.et_expiry);
        unitSpinner = findViewById(R.id.spinner_unit);
        Button save = findViewById(R.id.btn_save);
        Button clearDate = findViewById(R.id.btn_clear_date);

        unitSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));

        // The expiry field is not typed into - it opens a date picker, so bad formats are impossible
        expiryInput.setOnClickListener(v -> showDatePicker());
        clearDate.setOnClickListener(v -> expiryInput.setText(""));
        save.setOnClickListener(v -> saveItem());

        editingId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (editingId != -1) {
            setTitle(R.string.title_edit_ingredient);
            loadForEditing(editingId);
        } else {
            setTitle(R.string.title_add_ingredient);
        }
    }

    private void loadForEditing(long id) {
        PantryItem item = db.getPantryItem(id);
        if (item == null) { // row was deleted meanwhile
            Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        nameInput.setText(item.getName());
        qtyInput.setText(Format.qty(item.getQuantity()));
        expiryInput.setText(item.getExpiryDate());
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equals(item.getUnit())) unitSpinner.setSelection(i);
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                expiryInput.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Validates the form; only writes to the database when every check passes. */
    private void saveItem() {
        nameLayout.setError(null);
        qtyLayout.setError(null);

        String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
        String qtyText = qtyInput.getText() == null ? "" : qtyInput.getText().toString().trim();
        String expiry = expiryInput.getText() == null ? "" : expiryInput.getText().toString().trim();

        boolean valid = true;

        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() < 2 || !name.matches("[\\p{L} '\\-]+")) {
            nameLayout.setError(getString(R.string.error_name_invalid));
            valid = false;
        }

        double qty = 0;
        if (qtyText.isEmpty()) {
            qtyLayout.setError(getString(R.string.error_qty_required));
            valid = false;
        } else {
            try {
                qty = Double.parseDouble(qtyText);
                if (qty <= 0 || qty > 100000) {
                    qtyLayout.setError(getString(R.string.error_qty_range));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                qtyLayout.setError(getString(R.string.error_qty_invalid));
                valid = false;
            }
        }

        if (!valid) return;

        String unit = (String) unitSpinner.getSelectedItem();
        if (editingId == -1) {
            db.insertPantryItem(new PantryItem(0, name, qty, unit, expiry));
            Toast.makeText(this, R.string.item_added, Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(new PantryItem(editingId, name, qty, unit, expiry));
            Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
