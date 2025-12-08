package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mdu.jobinvoicing.data.local.entities.TimeEntry;
import java.util.List;

public class TimeEntryAdapter extends RecyclerView.Adapter<TimeEntryAdapter.TimeEntryViewHolder> {

    private List<TimeEntry> timeEntries;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(TimeEntry timeEntry);
    }

    public TimeEntryAdapter(List<TimeEntry> timeEntries, OnDeleteClickListener onDeleteClickListener) {
        this.timeEntries = timeEntries;
        this.onDeleteClickListener = onDeleteClickListener;
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
        holder.timeEntryHoursTextView.setText("Hours: " + timeEntry.hours);
        holder.timeEntryRateTextView.setText("Rate: SEK " + timeEntry.hourRate + "/hr");
        holder.timeEntryTotalTextView.setText("Total: SEK" + timeEntry.total);
        holder.timeEntryNotesTextView.setText("Notes: " + timeEntry.notes);
        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(timeEntry);
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

        public TimeEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            timeEntryHoursTextView = itemView.findViewById(R.id.timeEntryHoursTextView);
            timeEntryRateTextView = itemView.findViewById(R.id.timeEntryRateTextView);
            timeEntryTotalTextView = itemView.findViewById(R.id.timeEntryTotalTextView);
            timeEntryNotesTextView = itemView.findViewById(R.id.timeEntryNotesTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
