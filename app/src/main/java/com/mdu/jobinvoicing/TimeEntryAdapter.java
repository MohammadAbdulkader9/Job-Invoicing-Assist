package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mdu.jobinvoicing.data.local.entities.TimerItem;
import java.util.List;

public class TimeEntryAdapter extends RecyclerView.Adapter<TimeEntryAdapter.TimeEntryViewHolder> {

    private List<TimerItem> timeEntries;

    public TimeEntryAdapter(List<TimerItem> timeEntries) {
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
        TimerItem timeEntry = timeEntries.get(position);
        holder.timeEntryHoursTextView.setText("Hours: 1"); // Placeholder
        holder.timeEntryRateTextView.setText("Rate: $50/hr"); // Placeholder
        holder.timeEntryNotesTextView.setText("Notes: " + timeEntry.getName());
    }

    @Override
    public int getItemCount() {
        return timeEntries.size();
    }

    public void addTimeEntry(TimerItem timeEntry) {
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
