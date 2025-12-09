package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mdu.jobinvoicing.data.local.entities.ServiceItem;
import java.util.List;

public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.ServiceItemViewHolder> {

    private List<ServiceItem> serviceItems;

    public ServiceItemAdapter(List<ServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
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
        holder.serviceItemDescriptionTextView.setText("Service: " + serviceItem.description);
        holder.serviceItemCostTextView.setText("Fee: SEK " + serviceItem.serviceFee);
        holder.serviceItemNotesTextView.setText("Notes: " + serviceItem.notes);
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

        public ServiceItemViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceItemDescriptionTextView = itemView.findViewById(R.id.serviceItemDescriptionTextView);
            serviceItemCostTextView = itemView.findViewById(R.id.serviceItemCostTextView);
            serviceItemNotesTextView = itemView.findViewById(R.id.serviceItemNotesTextView);
        }
    }
}
