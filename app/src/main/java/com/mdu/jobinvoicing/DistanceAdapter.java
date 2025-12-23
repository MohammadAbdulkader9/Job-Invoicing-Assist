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
import com.mdu.jobinvoicing.data.local.entities.Distance;

import java.util.List;
import java.util.Locale;

public class DistanceAdapter extends RecyclerView.Adapter<DistanceAdapter.DistanceViewHolder> {

    private List<Distance> distances;
    private final OnDeleteClickListener onDeleteClickListener;
    private final String workPlanId;
    private final String operatorId;
    private FirebaseFirestore db;
    private String userId;

    public interface OnDeleteClickListener {
        void onDeleteClick(Distance distance);
    }

    public DistanceAdapter(List<Distance> distances, String workPlanId, String operatorId, OnDeleteClickListener onDeleteClickListener) {
        this.distances = distances;
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
    public DistanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distance, parent, false);
        return new DistanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistanceViewHolder holder, int position) {
        Distance distance = distances.get(position);

        holder.distanceTextView.setText("Distance: " + distance.getDistance() + " " + distance.getUnit());
        holder.distanceUnitTextView.setText("Unit: " + distance.getUnit());
        holder.distanceRateTextView.setText("Rate: SEK " + distance.getRate() + "/" + distance.getUnit());
        holder.distanceTotalTextView.setText("Total: SEK " + String.format(Locale.getDefault(), "%.2f", distance.getDistance() * distance.getRate()));
        holder.distanceNotesTextView.setText("Notes: " + distance.getNotes());

        holder.deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Distance")
                    .setMessage("Are you sure you want to delete this distance?")
                    .setPositiveButton("Delete", (d, which) ->{
                         if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDeleteClick(distance);
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
            showEditDistanceDialog(distance, holder.itemView.getContext());
        });
    }

    private void showEditDistanceDialog(Distance distance, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_distance, null);
        builder.setView(dialogView);

        final TextInputEditText editDistanceEditText = dialogView.findViewById(R.id.editDistance);
        final TextInputEditText editUnitEditText = dialogView.findViewById(R.id.editDistanceUnit);
        final TextInputEditText editRateEditText = dialogView.findViewById(R.id.editDistanceRatePrice);
        final TextInputEditText editNotesEditText = dialogView.findViewById(R.id.editDistanceNotes);

        editDistanceEditText.setText(String.valueOf(distance.getDistance()));
        editUnitEditText.setText(distance.getUnit());
        editRateEditText.setText(String.valueOf(distance.getRate()));
        editNotesEditText.setText(distance.getNotes());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String distanceText = editDistanceEditText.getText().toString();
            String unit = editUnitEditText.getText().toString();
            String rate = editRateEditText.getText().toString();
            String notesText = editNotesEditText.getText().toString();

            distance.setDistance(Double.parseDouble(distanceText));
            distance.setUnit(unit);
            distance.setRate(Double.parseDouble(rate));
            distance.setNotes(notesText);

            updateDistanceInFirebase(distance);
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


    private void updateDistanceInFirebase(Distance distance) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("users").document(userId)
            .collection("operators").document(operatorId)
            .collection("workplans").document(workPlanId)
            .collection("distances").document(distance.getId())
            .update(
                    "distance", distance.getDistance(),
                    "unit", distance.getUnit(),
                    "rate", distance.getRate(),
                    "total", distance.getDistance() * distance.getRate(),
                    "notes", distance.getNotes()
            )
            .addOnSuccessListener(aVoid -> {
                int index = distances.indexOf(distance);
                if (index != -1) {
                    distances.set(index, distance);
                    notifyItemChanged(index);
                }
            });
}


    @Override
    public int getItemCount() {
        return distances.size();
    }

    public void setDistances(List<Distance> distances) {
        this.distances = distances;
        notifyDataSetChanged();
    }

    static class DistanceViewHolder extends RecyclerView.ViewHolder {
        TextView distanceTextView;
        TextView distanceUnitTextView;
        TextView distanceRateTextView;
        TextView distanceTotalTextView;
        TextView distanceNotesTextView;
        ImageButton deleteButton;
        ImageButton editButton;


        public DistanceViewHolder(@NonNull View itemView) {
            super(itemView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            distanceUnitTextView = itemView.findViewById(R.id.distanceUnitTextView);
            distanceRateTextView = itemView.findViewById(R.id.distanceRateTextView);
            distanceTotalTextView = itemView.findViewById(R.id.distanceTotalTextView);
            distanceNotesTextView = itemView.findViewById(R.id.distanceNotesTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
