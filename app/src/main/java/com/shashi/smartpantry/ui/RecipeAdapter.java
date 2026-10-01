package com.shashi.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shashi.smartpantry.R;
import com.shashi.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Lists recipes. An optional per-recipe note (e.g. "Missing: butter") can be shown under the name. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClick {
        void onClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final Map<Long, String> notes = new HashMap<>();
    private final OnRecipeClick listener;

    public RecipeAdapter(OnRecipeClick listener) {
        this.listener = listener;
    }

    public void setRecipes(List<Recipe> newRecipes, Map<Long, String> newNotes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notes.clear();
        if (newNotes != null) notes.putAll(newNotes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final Recipe r = recipes.get(position);
        h.name.setText(r.getName());
        String note = notes.get(r.getId());
        if (note == null) {
            h.note.setText(h.itemView.getContext().getString(
                    R.string.ingredient_count, r.getIngredients().size()));
        } else {
            h.note.setText(note);
        }
        h.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, note;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tv_recipe_name);
            note = v.findViewById(R.id.tv_recipe_note);
        }
    }
}
