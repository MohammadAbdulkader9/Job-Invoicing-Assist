package com.mdu.jobinvoicing;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<LoadTicket> tickets;
    private OnItemClickListener listener; // Listener interface

    public TicketAdapter(List<LoadTicket> tickets) {
        this.tickets = tickets;
    }

    // Interface to handle item clicks
    public interface OnItemClickListener {
        void onItemClick(LoadTicket ticket);
    }

    // Method to set the listener
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

        holder.tvDetails.setText(
                "Created: " + t.created + "\n" +
                        "Target Name: " + t.targetName + "\n" +
                        "Creator Target: " + t.creatorTargetName + "\n" +
                        "Operator: " + t.operator + "\n" +
                        "Weight: " + t.weight + "\n" +
                        "Notes: " + t.freeText + "\n" +
                        "Materials: " + t.materialNames + "\n" +
                        "Load Count: " + t.loadCount + "\n" +
                        "Local ID: " + t.localId + "\n" +
                        "Customer: " + t.customer + "\n" +
                        "Project Ref: " + t.projectReference
        );

        // Set click listener on the entire item
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
        TextView tvDetails;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDetails = itemView.findViewById(R.id.tvTicketItem);
        }
    }
}
