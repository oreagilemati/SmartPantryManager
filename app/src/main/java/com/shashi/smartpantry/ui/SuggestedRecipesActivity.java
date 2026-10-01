package com.shashi.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shashi.smartpantry.R;
import com.shashi.smartpantry.db.DatabaseHelper;
import com.shashi.smartpantry.logic.RecipeMatcher;
import com.shashi.smartpantry.model.PantryItem;
import com.shashi.smartpantry.model.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Suggested Recipes screen. Runs the STRICT matching rule against the current
 * pantry and lists only recipes the user can cook right now. The optional
 * "Almost There" list is kept in its own, clearly separated section.
 */
public class SuggestedRecipesActivity extends BaseActivity implements RecipeAdapter.OnRecipeClick {

    private DatabaseHelper db;
    private RecipeAdapter strictAdapter, almostAdapter;
    private TextView emptyView, almostHeader;
    private RecyclerView strictList, almostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle(R.string.title_suggested);

        db = new DatabaseHelper(this);
        emptyView = findViewById(R.id.tv_no_matches);
        almostHeader = findViewById(R.id.tv_almost_header);
        strictList = findViewById(R.id.rv_strict);
        almostList = findViewById(R.id.rv_almost);

        strictAdapter = new RecipeAdapter(this);
        almostAdapter = new RecipeAdapter(this);
        strictList.setLayoutManager(new LinearLayoutManager(this));
        strictList.setAdapter(strictAdapter);
        almostList.setLayoutManager(new LinearLayoutManager(this));
        almostList.setAdapter(almostAdapter);

        setupBottomNav(R.id.nav_suggested);
    }

    /** Recalculate on every return so changes to the pantry are reflected immediately. */
    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> all = db.getAllRecipes();
        List<PantryItem> pantry = db.getAllPantryItems();

        List<Recipe> strict = RecipeMatcher.strictMatches(all, pantry);
        strictAdapter.setRecipes(strict, null);

        boolean noMatches = strict.isEmpty();
        emptyView.setVisibility(noMatches ? View.VISIBLE : View.GONE);
        strictList.setVisibility(noMatches ? View.GONE : View.VISIBLE);

        // Optional bonus feature, separate from the strict list and switched on in Settings
        if (AppSettings.showAlmostThere(this)) {
            List<Recipe> almost = RecipeMatcher.almostThere(all, pantry);
            Map<Long, String> notes = new HashMap<>();
            for (Recipe r : almost) {
                List<String> missing = RecipeMatcher.missingIngredients(r, pantry);
                notes.put(r.getId(), getString(R.string.missing_one, missing.get(0)));
            }
            almostAdapter.setRecipes(almost, notes);
            int visibility = almost.isEmpty() ? View.GONE : View.VISIBLE;
            almostHeader.setVisibility(visibility);
            almostList.setVisibility(visibility);
        } else {
            almostHeader.setVisibility(View.GONE);
            almostList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
