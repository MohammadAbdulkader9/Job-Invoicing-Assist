package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mdu.jobinvoicing.data.local.entities.Material;
import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private List<Material> materials;

    public MaterialAdapter(List<Material> materials) {
        this.materials = materials;
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
        holder.materialUnitPriceTextView.setText("Unit Price: $" + material.unitPrice);
        holder.materialNotesTextView.setText("Notes: " + material.notes);
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
        TextView materialNotesTextView;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            materialNameTextView = itemView.findViewById(R.id.materialNameTextView);
            materialQuantityTextView = itemView.findViewById(R.id.materialQuantityTextView);
            materialUnitPriceTextView = itemView.findViewById(R.id.materialUnitPriceTextView);
            materialNotesTextView = itemView.findViewById(R.id.materialNotesTextView);
        }
    }
}
