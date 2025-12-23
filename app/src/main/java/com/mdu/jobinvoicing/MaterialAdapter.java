package com.mdu.jobinvoicing;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mdu.jobinvoicing.data.local.entities.Material;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private List<Material> materials;
    private final OnDeleteClickListener onDeleteClickListener;
    private final String workPlanId;
    private final String operatorId;
    private FirebaseFirestore db;
    private String userId;

    public interface OnDeleteClickListener {
        void onDeleteClick(Material material);
    }

    public MaterialAdapter(List<Material> materials, String workPlanId, String operatorId, OnDeleteClickListener onDeleteClickListener) {
        this.materials = materials;
        this.workPlanId = workPlanId;
        this.operatorId = operatorId;
        this.onDeleteClickListener = onDeleteClickListener;
        this.db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
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
        holder.materialUnitPriceTextView.setText("Unit Price: SEK " + material.getUnitPrice() + "/" + material.getUnit());
        holder.materialTotalTextView.setText("Total: SEK " + String.format(Locale.getDefault(), "%.2f", material.getTotal()));
        holder.materialNotesTextView.setText("Notes: " + material.getNotes());

        holder.deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Material")
                    .setMessage("Are you sure you want to delete this material?")
                    .setPositiveButton("Delete", (d, which) -> {
                        if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDeleteClick(material);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            // Set button text colors AFTER show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(Color.BLACK);

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(Color.BLACK);
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
        final TextInputEditText editMaterialUnit = dialogView.findViewById(R.id.editMaterialUnit);
        final TextInputEditText editMaterialUnitPrice = dialogView.findViewById(R.id.editMaterialUnitPrice);
        final TextInputEditText editMaterialNotes = dialogView.findViewById(R.id.editMaterialNotes);

        editMaterialName.setText(material.getName());
        editMaterialQuantity.setText(String.valueOf(material.getQuantity()));
        editMaterialUnit.setText(material.getUnit());
        editMaterialUnitPrice.setText(String.valueOf(material.getUnitPrice()));
        editMaterialNotes.setText(material.getNotes());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = editMaterialName.getText().toString();
            BigDecimal quantity = new BigDecimal(editMaterialQuantity.getText().toString());
            String unit = editMaterialUnit.getText().toString();
            BigDecimal unitPrice = new BigDecimal(editMaterialUnitPrice.getText().toString());
            String notes = editMaterialNotes.getText().toString();

            material.setName(name);
            material.setQuantity(quantity.doubleValue());
            material.setUnit(unit);
            material.setUnitPrice(unitPrice.doubleValue());
            material.setNotes(notes);
            material.setTotal(quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP).doubleValue());

            updateMaterialInFirebase(material);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        // ✅ Set button text color AFTER show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(Color.BLACK);

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(Color.BLACK);

    }

    private void updateMaterialInFirebase(Material material) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("operators").document(operatorId).collection("workplans").document(workPlanId)
                .collection("materials").document(material.getId())
                .update(
                        "name", material.getName(),
                        "quantity", material.getQuantity(),
                        "unit", material.getUnit(),
                        "unitPrice", material.getUnitPrice(),
                        "notes", material.getNotes(),
                        "total", material.getTotal()
                )
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

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
        notifyDataSetChanged();
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
