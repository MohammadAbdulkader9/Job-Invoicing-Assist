package com.mdu.jobinvoicing;

import android.app.AlertDialog;
import android.content.Context;
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
import com.mdu.jobinvoicing.data.local.entities.Weight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private List<Weight> weights;
    private final OnDeleteClickListener onDeleteClickListener;
    private final String workPlanId;
    private final String operatorId;
    private FirebaseFirestore db;
    private String userId;

    public interface OnDeleteClickListener {
        void onDeleteClick(Weight weight);
    }

    public WeightAdapter(List<Weight> weights, String workPlanId, String operatorId, OnDeleteClickListener onDeleteClickListener) {
        this.weights = weights;
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
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weight, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        Weight weight = weights.get(position);

        holder.weightTextView.setText("Weight: " + weight.getWeight() + " " + weight.getUnit());
        holder.weightUnitTextView.setText("Unit: " + weight.getUnit());
        holder.weightRateTextView.setText("Rate: SEK " + weight.getRate() + "/" + weight.getUnit());
        holder.weightTotalTextView.setText("Total: SEK " + String.format(Locale.getDefault(), "%.2f", weight.getTotal()));
        holder.weightNotesTextView.setText("Notes: " + weight.getNotes());

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Weight")
                    .setMessage("Are you sure you want to delete this weight?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDeleteClick(weight);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.editButton.setOnClickListener(v -> {
            showEditWeightDialog(weight, holder.itemView.getContext());
        });
    }

    private void showEditWeightDialog(Weight weight, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_weight, null);
        builder.setView(dialogView);

        final TextInputEditText editWeight = dialogView.findViewById(R.id.editWeight);
        final TextInputEditText editUnit = dialogView.findViewById(R.id.editWeightUnit);
        final TextInputEditText editRate = dialogView.findViewById(R.id.editWeightRatePrice);
        final TextInputEditText editNotes = dialogView.findViewById(R.id.editWeightNotes);

        editWeight.setText(String.valueOf(weight.getWeight()));
        editUnit.setText(weight.getUnit());
        editRate.setText(String.valueOf(weight.getRate()));
        editNotes.setText(weight.getNotes());

        builder.setPositiveButton("Save", (dialog, which) -> {
            BigDecimal weightValue = new BigDecimal(editWeight.getText().toString());
            String unit = editUnit.getText().toString();
            BigDecimal rate = new BigDecimal(editRate.getText().toString());
            String notes = editNotes.getText().toString();

            weight.setWeight(weightValue.doubleValue());
            weight.setUnit(unit);
            weight.setRate(rate.doubleValue());
            weight.setNotes(notes);
            weight.setTotal(weightValue.multiply(rate).setScale(2, RoundingMode.HALF_UP).doubleValue());

            updateWeightInFirebase(weight);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateWeightInFirebase(Weight weight) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .collection("operators").document(operatorId)
                .collection("workplans").document(workPlanId)
                .collection("weights").document(weight.getId())
                .update(
                        "weight", weight.getWeight(),
                        "unit", weight.getUnit(),
                        "rate", weight.getRate(),
                        "notes", weight.getNotes(),
                        "total", weight.getTotal()
                )
                .addOnSuccessListener(aVoid -> {
                    int index = weights.indexOf(weight);
                    if (index != -1) {
                        weights.set(index, weight);
                        notifyItemChanged(index);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return weights.size();
    }

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
        notifyDataSetChanged();
    }

    static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView weightTextView;
        TextView weightUnitTextView;
        TextView weightRateTextView;
        TextView weightTotalTextView;
        TextView weightNotesTextView;
        ImageButton deleteButton;
        ImageButton editButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            weightUnitTextView = itemView.findViewById(R.id.weightUnitTextView);
            weightRateTextView = itemView.findViewById(R.id.weightRateTextView);
            weightTotalTextView = itemView.findViewById(R.id.weightTotalTextView);
            weightNotesTextView = itemView.findViewById(R.id.weightNotesTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
