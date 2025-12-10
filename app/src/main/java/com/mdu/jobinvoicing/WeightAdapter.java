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
import com.google.firebase.firestore.FirebaseFirestore;
import com.mdu.jobinvoicing.data.local.entities.Weight;

import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private List<Weight> weights;
    private final OnDeleteClickListener onDeleteClickListener;
    private final String workPlanId;

    public interface OnDeleteClickListener {
        void onDeleteClick(Weight weight);
    }

    public WeightAdapter(List<Weight> weights,String workPlanId, OnDeleteClickListener onDeleteClickListener) {
        this.weights = weights;
        this.workPlanId = workPlanId;
        this.onDeleteClickListener = onDeleteClickListener;
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

        holder.weightTextView.setText("Weight: " + weight.getWeight());
        holder.weightUnitTextView.setText("Unit: " +weight.getUnit());
        holder.weightRateTextView.setText("Rate: Sek " + weight.getRate() + " " + weight.getUnit());
        holder.weightTotalTextView.setText("Total: Sek " + weight.getTotal() + " " + weight.getUnit());
        holder.weightNotesTextView.setText(weight.getNotes());

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(weight);
            }
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

        final TextInputEditText editWeightEditText = dialogView.findViewById(R.id.weightEditText);
        final TextInputEditText editRatePerUnitEditText = dialogView.findViewById(R.id.weightRateEditText);
        final TextInputEditText editNotesEditText = dialogView.findViewById(R.id.weightNotesEditText);

        editWeightEditText.setText(String.valueOf(weight.getWeight()));
        editRatePerUnitEditText.setText(String.valueOf(weight.getRate()));
        editNotesEditText.setText(weight.getNotes());


        builder.setPositiveButton("Save", (dialog, which) -> {
            String weightText = editWeightEditText.getText().toString();
            String ratePerUnitText = editRatePerUnitEditText.getText().toString();
            String notesText = editNotesEditText.getText().toString();


            weight.setWeight(Double.parseDouble(weightText));
            weight.setRate(Double.parseDouble(ratePerUnitText));
            weight.setNotes(notesText);

            updateWeightInFirebase(weight);
    });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateWeightInFirebase(Weight weight) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("workPlans").document(workPlanId)
                .collection("weights").document(weight.getId())
                .update(
                        "weight", weight.getWeight(),
                        "rate", weight.getRate(),
                        "unit", weight.getUnit(),
                        "total", weight.getWeight() * weight.getRate(),
                        "notes", weight.getNotes()
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
        TextView weightRateTextView;
        TextView weightNotesTextView;
        TextView weightTotalTextView;
        TextView weightUnitTextView;

        ImageButton deleteButton;
        ImageButton editButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            weightRateTextView = itemView.findViewById(R.id.weightRateTextView);
            weightUnitTextView = itemView.findViewById(R.id.weightUnitPriceTextView);
            weightTotalTextView = itemView.findViewById(R.id.weightTotalTextView);
            weightNotesTextView = itemView.findViewById(R.id.weightNotesTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
