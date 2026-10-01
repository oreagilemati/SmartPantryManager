package com.shashi.smartpantry.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shashi.smartpantry.R;
import com.shashi.smartpantry.db.DatabaseHelper;
import com.shashi.smartpantry.logic.RecipeMatcher;
import com.shashi.smartpantry.model.PantryItem;
import com.shashi.smartpantry.model.Recipe;
import com.shashi.smartpantry.model.RecipeIngredient;

import java.util.List;

/** Recipe Detail screen: full ingredient list (with have / missing marks) and the method. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        DatabaseHelper db = new DatabaseHelper(this);
        try {
            Recipe recipe = db.getRecipe(id);
            if (recipe == null) {
                Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            List<PantryItem> pantry = db.getAllPantryItems();
            List<String> missing = RecipeMatcher.missingIngredients(recipe, pantry);

            setTitle(recipe.getName());
            ((TextView) findViewById(R.id.tv_detail_name)).setText(recipe.getName());

            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient ing : recipe.getIngredients()) {
                boolean have = !missing.contains(ing.getName());
                sb.append(have ? "\u2714  " : "\u2718  ")
                  .append(Format.qty(ing.getQuantity())).append(' ')
                  .append(ing.getUnit()).append("  ")
                  .append(ing.getName()).append('\n');
            }
            ((TextView) findViewById(R.id.tv_detail_ingredients)).setText(sb.toString().trim());
            ((TextView) findViewById(R.id.tv_detail_steps)).setText(recipe.getSteps());
        } finally {
            db.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
