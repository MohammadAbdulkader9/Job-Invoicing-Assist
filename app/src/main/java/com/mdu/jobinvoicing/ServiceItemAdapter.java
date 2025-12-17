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
import com.mdu.jobinvoicing.data.local.entities.ServiceItem;

import java.util.List;
import java.util.Locale;

public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.ServiceItemViewHolder> {

    private List<ServiceItem> serviceItems;
    private OnDeleteClickListener onDeleteClickListener;
    private final String workPlanId;
    private FirebaseFirestore db;
    private String userId;

    public interface OnDeleteClickListener {
        void onDeleteClick(ServiceItem serviceItem);
    }

    public ServiceItemAdapter(List<ServiceItem> serviceItems, String workPlanId, OnDeleteClickListener onDeleteClickListener) {
        this.serviceItems = serviceItems;
        this.workPlanId = workPlanId;
        this.onDeleteClickListener = onDeleteClickListener;
        this.db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @NonNull
    @Override
    public ServiceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_item, parent, false);
        return new ServiceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceItemViewHolder holder, int position) {
        ServiceItem serviceItem = serviceItems.get(position);
        holder.serviceItemDescriptionTextView.setText("Service: " + serviceItem.getDescription());
        holder.serviceItemCostTextView.setText("Fee: SEK " + String.format(Locale.getDefault(), "%.2f", serviceItem.getServiceFee()));
        holder.serviceItemNotesTextView.setText("Notes: " + serviceItem.getNotes());
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Service Item")
                    .setMessage("Are you sure you want to delete this service item?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDeleteClick(serviceItem);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.editButton.setOnClickListener(v -> {
            showEditServiceItemDialog(serviceItem, holder.itemView.getContext());
        });
    }

    private void showEditServiceItemDialog(ServiceItem serviceItem, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_service_item, null);
        builder.setView(dialogView);

        final TextInputEditText editServiceItemDescription = dialogView.findViewById(R.id.editServiceItemDescription);
        final TextInputEditText editServiceItemCost = dialogView.findViewById(R.id.editServiceItemCost);
        final TextInputEditText editServiceItemNotes = dialogView.findViewById(R.id.editServiceItemNotes);

        editServiceItemDescription.setText(serviceItem.getDescription());
        editServiceItemCost.setText(String.valueOf(serviceItem.getServiceFee()));
        editServiceItemNotes.setText(serviceItem.getNotes());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String description = editServiceItemDescription.getText().toString();
            double cost = Double.parseDouble(editServiceItemCost.getText().toString());
            String notes = editServiceItemNotes.getText().toString();

            serviceItem.setDescription(description);
            serviceItem.setServiceFee(cost);
            serviceItem.setNotes(notes);

            updateServiceItemInFirebase(serviceItem);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateServiceItemInFirebase(ServiceItem serviceItem) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("workplans").document(workPlanId)
                .collection("service_items").document(serviceItem.getId())
                .update(
                        "description", serviceItem.getDescription(),
                        "serviceFee", serviceItem.getServiceFee(),
                        "notes", serviceItem.getNotes()
                )
                .addOnSuccessListener(aVoid -> {
                    int index = serviceItems.indexOf(serviceItem);
                    if (index != -1) {
                        serviceItems.set(index, serviceItem);
                        notifyItemChanged(index);
                    }
                });
    }


    @Override
    public int getItemCount() {
        return serviceItems.size();
    }

    public void addServiceItem(ServiceItem serviceItem) {
        serviceItems.add(serviceItem);
        notifyItemInserted(serviceItems.size() - 1);
    }

    static class ServiceItemViewHolder extends RecyclerView.ViewHolder {
        TextView serviceItemDescriptionTextView;
        TextView serviceItemCostTextView;
        TextView serviceItemNotesTextView;
        ImageButton deleteButton;
        ImageButton editButton;

        public ServiceItemViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceItemDescriptionTextView = itemView.findViewById(R.id.serviceItemDescriptionTextView);
            serviceItemCostTextView = itemView.findViewById(R.id.serviceItemCostTextView);
            serviceItemNotesTextView = itemView.findViewById(R.id.serviceItemNotesTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
