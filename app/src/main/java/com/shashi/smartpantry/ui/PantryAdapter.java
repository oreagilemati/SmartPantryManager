package com.shashi.smartpantry.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shashi.smartpantry.R;
import com.shashi.smartpantry.logic.IngredientEmoji;
import com.shashi.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/** Binds the pantry rows from the database to item_pantry.xml. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private boolean highlightExpiry = true;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems, boolean highlightExpiry) {
        items.clear();
        items.addAll(newItems);
        this.highlightExpiry = highlightExpiry;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final PantryItem item = items.get(position);
        Context ctx = h.itemView.getContext();

        h.name.setText(IngredientEmoji.forName(item.getName()) + "  " + item.getName());
        h.quantity.setText(Format.qty(item.getQuantity()) + " " + item.getUnit());

        String expiry = item.getExpiryDate();
        if (expiry == null || expiry.isEmpty()) {
            h.expiry.setText(R.string.no_expiry);
            h.expiry.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        } else {
            int days = Format.daysUntil(expiry);
            int colour = R.color.text_secondary;
            String label = ctx.getString(R.string.expires_on, expiry);
            if (highlightExpiry && days < 0) {
                colour = R.color.expired;
                label = ctx.getString(R.string.expired_on, expiry);
            } else if (highlightExpiry && days <= 3) {
                colour = R.color.expiring_soon;
                label = ctx.getString(R.string.expiring_soon, expiry);
            }
            h.expiry.setText(label);
            h.expiry.setTextColor(ContextCompat.getColor(ctx, colour));
        }

        h.edit.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final ImageButton edit, delete;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tv_item_name);
            quantity = v.findViewById(R.id.tv_item_quantity);
            expiry = v.findViewById(R.id.tv_item_expiry);
            edit = v.findViewById(R.id.btn_edit);
            delete = v.findViewById(R.id.btn_delete);
        }
    }
}
