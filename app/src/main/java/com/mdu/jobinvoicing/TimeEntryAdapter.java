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
import com.mdu.jobinvoicing.data.local.entities.TimeEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class TimeEntryAdapter extends RecyclerView.Adapter<TimeEntryAdapter.TimeEntryViewHolder> {

    private List<TimeEntry> timeEntries;
    private OnDeleteClickListener onDeleteClickListener;
    private final String workPlanId;

    private FirebaseFirestore db;
    private String userId;

    public interface OnDeleteClickListener {
        void onDeleteClick(TimeEntry timeEntry);
    }

    public TimeEntryAdapter(List<TimeEntry> timeEntries, String workPlanId, OnDeleteClickListener onDeleteClickListener) {
        this.timeEntries = timeEntries;
        this.workPlanId = workPlanId;
        this.onDeleteClickListener = onDeleteClickListener;
        this.db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @NonNull
    @Override
    public TimeEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_entry, parent, false);
        return new TimeEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeEntryViewHolder holder, int position) {
        TimeEntry timeEntry = timeEntries.get(position);
        holder.timeEntryHoursTextView.setText("Hours: " + timeEntry.getHours());
        holder.timeEntryRateTextView.setText("Rate: SEK " + timeEntry.getHourRate() + "/hr");
        holder.timeEntryTotalTextView.setText("Total: SEK " + String.format(Locale.getDefault(), "%.2f", timeEntry.getTotal()));
        holder.timeEntryNotesTextView.setText("Notes: " + timeEntry.getNotes());
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Time Entry")
                    .setMessage("Are you sure you want to delete this time entry?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDeleteClick(timeEntry);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.editButton.setOnClickListener(v -> {
            showEditTimeEntryDialog(timeEntry, holder.itemView.getContext());
        });
    }

    private void showEditTimeEntryDialog(TimeEntry timeEntry, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_time_entry, null);
        builder.setView(dialogView);

        final TextInputEditText editTimeEntryHours = dialogView.findViewById(R.id.editTimeEntryHours);
        final TextInputEditText editTimeEntryRate = dialogView.findViewById(R.id.editTimeEntryRate);
        final TextInputEditText editTimeEntryNotes = dialogView.findViewById(R.id.editTimeEntryNotes);

        editTimeEntryHours.setText(String.valueOf(timeEntry.getHours()));
        editTimeEntryRate.setText(String.valueOf(timeEntry.getHourRate()));
        editTimeEntryNotes.setText(timeEntry.getNotes());

        builder.setPositiveButton("Save", (dialog, which) -> {
            BigDecimal hours = new BigDecimal(editTimeEntryHours.getText().toString());
            BigDecimal rate = new BigDecimal(editTimeEntryRate.getText().toString());
            String notes = editTimeEntryNotes.getText().toString();

            timeEntry.setHours(hours.doubleValue());
            timeEntry.setHourRate(rate.doubleValue());
            timeEntry.setNotes(notes);
            timeEntry.setTotal(hours.multiply(rate).setScale(2, RoundingMode.HALF_UP).doubleValue());

            updateTimeEntryInFirebase(timeEntry);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateTimeEntryInFirebase(TimeEntry timeEntry) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("workplans").document(workPlanId)
                .collection("time_entries").document(timeEntry.getId())
                .update(
                        "hours", timeEntry.getHours(),
                        "hourRate", timeEntry.getHourRate(),
                        "notes", timeEntry.getNotes(),
                        "total", timeEntry.getTotal()
                )
                .addOnSuccessListener(aVoid -> {
                    int index = timeEntries.indexOf(timeEntry);
                    if (index != -1) {
                        timeEntries.set(index, timeEntry);
                        notifyItemChanged(index);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return timeEntries.size();
    }

    public void addTimeEntry(TimeEntry timeEntry) {
        timeEntries.add(timeEntry);
        notifyItemInserted(timeEntries.size() - 1);
    }

    static class TimeEntryViewHolder extends RecyclerView.ViewHolder {
        TextView timeEntryHoursTextView;
        TextView timeEntryRateTextView;
        TextView timeEntryTotalTextView;
        TextView timeEntryNotesTextView;
        ImageButton deleteButton;
        ImageButton editButton;

        public TimeEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            timeEntryHoursTextView = itemView.findViewById(R.id.timeEntryHoursTextView);
            timeEntryRateTextView = itemView.findViewById(R.id.timeEntryRateTextView);
            timeEntryTotalTextView = itemView.findViewById(R.id.timeEntryTotalTextView);
            timeEntryNotesTextView = itemView.findViewById(R.id.timeEntryNotesTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
