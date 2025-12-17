package com.mdu.jobinvoicing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mdu.jobinvoicing.data.local.entities.WorkPlan;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkPlanAdapter extends RecyclerView.Adapter<WorkPlanAdapter.WorkPlanViewHolder> {

    private List<WorkPlan> workPlans;
    private Context context;
    private FirebaseFirestore db;
    private String userId;

    public WorkPlanAdapter(Context context, List<WorkPlan> workPlans) {
        this.context = context;
        this.workPlans = workPlans;
        this.db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @NonNull
    @Override
    public WorkPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_plan_item, parent, false);
        return new WorkPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkPlanViewHolder holder, int position) {
        ImageView delete = holder.itemView.findViewById(R.id.delete_icon);

        WorkPlan workPlan = workPlans.get(position);
        holder.clientNameTextView.setText(workPlan.clientName);
        holder.workTitleTextView.setText(workPlan.title);
        holder.workLocationTextView.setText(workPlan.location);

        if (workPlan.startDate > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.startDateTextView.setText(sdf.format(new Date(workPlan.startDate)));
        }

        if (workPlan.status != null) {
            //fix to get rid of _ in In_Progress
            if (workPlan.status == WorkPlanStatus.IN_PROGRESS) {
                holder.statusTextView.setText("IN PROGRESS");
            }else {
                holder.statusTextView.setText(workPlan.status.name());
            }
        }

        //work plan icon depending on status
        if (workPlan.status == WorkPlanStatus.OPEN) {
            holder.workStatusIcon.setImageResource(R.drawable.jobbs_list_status_open);
        } else if (workPlan.status == WorkPlanStatus.IN_PROGRESS) {
            holder.workStatusIcon.setImageResource(R.drawable.jobbs_list_status_in_progress);
        } else if (workPlan.status == WorkPlanStatus.CLOSED) {
            holder.workStatusIcon.setImageResource(R.drawable.jobbs_list_status_closed);
        }

        //delete icon press
        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            AlertDialog dialog = builder.setTitle("Delete Work Plan")
                    .setMessage("Are you sure you want to delete this work plan?")
                    .setPositiveButton("Yes", (d, which) -> {
                        int actualPosition = holder.getAdapterPosition();
                        if (actualPosition != RecyclerView.NO_POSITION) {
                            String workPlanId = workPlans.get(actualPosition).id;
                            db.collection("users").document(userId).collection("workplans").document(workPlanId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    workPlans.remove(actualPosition);
                                    notifyItemRemoved(actualPosition);
                                    Toast.makeText(context, "Work plan deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete work plan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        }
                    })
                    .setNegativeButton("No", (d, which) -> d.dismiss())
                    .show();
            
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.GREEN);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.RED);
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkPlanDetailsActivity.class);
            intent.putExtra("WORK_PLAN_ID", workPlan.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return workPlans.size();
    }

    static class WorkPlanViewHolder extends RecyclerView.ViewHolder {
        TextView clientNameTextView;
        TextView workTitleTextView;
        TextView workLocationTextView;
        TextView startDateTextView;
        TextView statusTextView;
        ImageView workStatusIcon;

        public WorkPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            clientNameTextView = itemView.findViewById(R.id.clientNameTextView);
            workTitleTextView = itemView.findViewById(R.id.workTitleTextView);
            workLocationTextView = itemView.findViewById(R.id.workLocationTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            workStatusIcon = itemView.findViewById(R.id.work_status_icon);
        }
    }
}
