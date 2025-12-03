package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mdu.jobinvoicing.data.local.entities.TimeEntry;
import java.util.List;

public class TimeEntryAdapter extends RecyclerView.Adapter<TimeEntryAdapter.TimeEntryViewHolder> {

    private List<TimeEntry> timeEntries;

    public TimeEntryAdapter(List<TimeEntry> timeEntries) {
        this.timeEntries = timeEntries;
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
        holder.timeEntryRateTextView.setText("Rate: $" + timeEntry.hourRate + "/hr");
        holder.timeEntryNotesTextView.setText("Notes: " + timeEntry.notes);
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
        TextView timeEntryNotesTextView;

        public TimeEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            timeEntryHoursTextView = itemView.findViewById(R.id.timeEntryHoursTextView);
            timeEntryRateTextView = itemView.findViewById(R.id.timeEntryRateTextView);
            timeEntryNotesTextView = itemView.findViewById(R.id.timeEntryNotesTextView);
        }
    }
}
