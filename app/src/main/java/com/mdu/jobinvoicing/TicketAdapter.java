package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<LoadTicket> tickets;
    private OnItemClickListener listener;

    public TicketAdapter(List<LoadTicket> tickets) {
        this.tickets = tickets;
    }

    // Interface to handle item clicks
    public interface OnItemClickListener {
        void onItemClick(LoadTicket ticket);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        LoadTicket t = tickets.get(position);

        holder.tvCreated.setText("Created: " + t.created);
        holder.tvTargetName.setText("Target Name: " + t.targetName);

        holder.tvOperator.setText("Operator: " + t.operator);
        holder.tvWeight.setText("Weight: " + t.weight);
        holder.tvNotes.setText("Notes: " + t.freeText);
        holder.tvMaterials.setText("Materials: " + t.materialNames);
        holder.tvCustomer.setText("Customer: " + t.customer);
        holder.tvProject.setText("Project Ref: " + t.projectReference);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onItemClick(tickets.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView tvCreated, tvTargetName, tvTarget, tvOperator, tvWeight,
                tvNotes, tvMaterials, tvCustomer, tvProject;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCreated = itemView.findViewById(R.id.tvCreated);
            tvTargetName = itemView.findViewById(R.id.tvTargetName);
            tvTarget = itemView.findViewById(R.id.tvTarget);
            tvOperator = itemView.findViewById(R.id.tvOperator);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvMaterials = itemView.findViewById(R.id.tvMaterials);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvProject = itemView.findViewById(R.id.tvProject);
        }
    }
}
