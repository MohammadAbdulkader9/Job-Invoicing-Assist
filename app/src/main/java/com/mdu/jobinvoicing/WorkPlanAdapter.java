package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mdu.jobinvoicing.data.local.entities.WorkPlan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkPlanAdapter extends RecyclerView.Adapter<WorkPlanAdapter.WorkPlanViewHolder> {

    private List<WorkPlan> workPlans;

    public WorkPlanAdapter(List<WorkPlan> workPlans) {
        this.workPlans = workPlans;
    }

    @NonNull
    @Override
    public WorkPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_plan_item, parent, false);
        return new WorkPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkPlanViewHolder holder, int position) {
        WorkPlan workPlan = workPlans.get(position);
        holder.clientNameTextView.setText(workPlan.clientName);
        holder.workTitleTextView.setText(workPlan.title);
        holder.workDescriptionTextView.setText(workPlan.description);
        holder.workLocationTextView.setText(workPlan.location);

        if (workPlan.startDate > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.startDateTextView.setText(sdf.format(new Date(workPlan.startDate)));
        }

        if (workPlan.status != null) {
            holder.statusTextView.setText(workPlan.status.name());
        }
    }

    @Override
    public int getItemCount() {
        return workPlans.size();
    }

    static class WorkPlanViewHolder extends RecyclerView.ViewHolder {
        TextView clientNameTextView;
        TextView workTitleTextView;
        TextView workDescriptionTextView;
        TextView workLocationTextView;
        TextView startDateTextView;
        TextView statusTextView;

        public WorkPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            clientNameTextView = itemView.findViewById(R.id.clientNameTextView);
            workTitleTextView = itemView.findViewById(R.id.workTitleTextView);
            workDescriptionTextView = itemView.findViewById(R.id.workDescriptionTextView);
            workLocationTextView = itemView.findViewById(R.id.workLocationTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}
