package com.mdu.jobinvoicing;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mdu.jobinvoicing.data.local.entities.Material;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private List<Material> materials;
    private OnDeleteClickListener onDeleteClickListener;
    private final String workPlanId;

    public interface OnDeleteClickListener {
        void onDeleteClick(Material material);
    }

    public MaterialAdapter(List<Material> materials, String workPlanId, OnDeleteClickListener onDeleteClickListener) {
        this.materials = materials;
        this.workPlanId = workPlanId;
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
        holder.materialNameTextView.setText("Material: " + material.getName());
        holder.materialQuantityTextView.setText("Quantity: " + material.getQuantity() + " " + material.getUnit());
        holder.materialUnitPriceTextView.setText("Unit Price: SEK " + material.getUnitPrice());
        holder.materialTotalTextView.setText("Total: SEK " + material.getTotal());
        holder.materialNotesTextView.setText("Notes: " + material.getNotes());

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(material);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            showEditMaterialDialog(material, holder.itemView.getContext());
        });
    }

    private void showEditMaterialDialog(Material material, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_material, null);
        builder.setView(dialogView);

        final TextInputEditText editMaterialName = dialogView.findViewById(R.id.editMaterialName);
        final TextInputEditText editMaterialQuantity = dialogView.findViewById(R.id.editMaterialQuantity);
        final TextInputEditText editMaterialUnitPrice = dialogView.findViewById(R.id.editMaterialUnitPrice);
        final TextInputEditText editMaterialNotes = dialogView.findViewById(R.id.editMaterialNotes);

        editMaterialName.setText(material.getName());
        editMaterialQuantity.setText(String.valueOf(material.getQuantity()));
        editMaterialUnitPrice.setText(String.valueOf(material.getUnitPrice()));
        editMaterialNotes.setText(material.getNotes());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = editMaterialName.getText().toString();
            double quantity = Double.parseDouble(editMaterialQuantity.getText().toString());
            double unitPrice = Double.parseDouble(editMaterialUnitPrice.getText().toString());
            String notes = editMaterialNotes.getText().toString();

            material.setName(name);
            material.setQuantity(quantity);
            material.setUnitPrice(unitPrice);
            material.setNotes(notes);
            material.setTotal(quantity * unitPrice);

            updateMaterialInFirebase(material);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateMaterialInFirebase(Material material) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("workplans").document(workPlanId)
                .collection("materials").document(material.getMaterialId())
                .set(material)
                .addOnSuccessListener(aVoid -> {
                    int index = materials.indexOf(material);
                    if (index != -1) {
                        materials.set(index, material);
                        notifyItemChanged(index);
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
        ImageButton editButton;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            materialNameTextView = itemView.findViewById(R.id.materialNameTextView);
            materialQuantityTextView = itemView.findViewById(R.id.materialQuantityTextView);
            materialUnitPriceTextView = itemView.findViewById(R.id.materialUnitPriceTextView);
            materialTotalTextView = itemView.findViewById(R.id.materialTotalTextView);
            materialNotesTextView = itemView.findViewById(R.id.materialNotesTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
