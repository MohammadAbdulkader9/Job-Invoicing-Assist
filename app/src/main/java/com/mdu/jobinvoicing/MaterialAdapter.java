package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mdu.jobinvoicing.data.local.entities.Material;
import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private List<Material> materials;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Material material);
    }

    public MaterialAdapter(List<Material> materials, OnDeleteClickListener onDeleteClickListener) {
        this.materials = materials;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material material = materials.get(position);
        holder.materialNameTextView.setText("Material: " + material.name);
        holder.materialQuantityTextView.setText("Quantity: " + material.quantity + " " + material.unit);
        holder.materialUnitPriceTextView.setText("Unit Price: SEK " + material.unitPrice);
        holder.materialTotalTextView.setText("Total: SEK " + material.total);
        holder.materialNotesTextView.setText("Notes: " + material.notes);
        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(material);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    public void addMaterial(Material material) {
        materials.add(material);
        notifyItemInserted(materials.size() - 1);
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView materialNameTextView;
        TextView materialQuantityTextView;
        TextView materialUnitPriceTextView;
        TextView materialTotalTextView;
        TextView materialNotesTextView;
        ImageButton deleteButton;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            materialNameTextView = itemView.findViewById(R.id.materialNameTextView);
            materialQuantityTextView = itemView.findViewById(R.id.materialQuantityTextView);
            materialUnitPriceTextView = itemView.findViewById(R.id.materialUnitPriceTextView);
            materialTotalTextView = itemView.findViewById(R.id.materialTotalTextView);
            materialNotesTextView = itemView.findViewById(R.id.materialNotesTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
