package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mdu.jobinvoicing.data.local.entities.Operator;

import java.util.List;

public class OperatorAdapter extends RecyclerView.Adapter<OperatorAdapter.OperatorViewHolder> {

    private List<Operator> operatorList;
    private OnOperatorClickListener listener;

    public interface OnOperatorClickListener {
        void onSetActive(Operator operator);
        void onEdit(Operator operator);
        void onDelete(Operator operator);
    }

    public OperatorAdapter(List<Operator> operatorList, OnOperatorClickListener listener) {
        this.operatorList = operatorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OperatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.operator_card, parent, false);
        return new OperatorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OperatorViewHolder holder, int position) {
        Operator operator = operatorList.get(position);

        holder.fullName.setText(operator.name);
        holder.role.setText(operator.role);
        holder.email.setText("✉ " + operator.email);
        holder.phone.setText("📞 " + operator.phone);

        // Show active badge if operator is active
        if (operator.isActive) {
            holder.activeTag.setVisibility(View.VISIBLE);
            holder.btnSetActive.setVisibility(View.GONE);
            holder.cardContainer.setBackgroundResource(R.drawable.active_border);
        } else {
            holder.activeTag.setVisibility(View.GONE);
            holder.btnSetActive.setVisibility(View.VISIBLE);
            holder.cardContainer.setBackgroundResource(R.drawable.normal_border);
        }

        // Set Active button
        holder.btnSetActive.setOnClickListener(v -> listener.onSetActive(operator));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(operator));

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(operator));
    }

    @Override
    public int getItemCount() {
        return operatorList.size();
    }

    public void updateList(List<Operator> newList) {
        operatorList = newList;
        notifyDataSetChanged();
    }

    public static class OperatorViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, role, email, phone, activeTag;
        Button btnSetActive, btnEdit;
        ImageButton btnDelete;
        View cardContainer;

        public OperatorViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.tvName);
            role = itemView.findViewById(R.id.tvRole);
            email = itemView.findViewById(R.id.tvEmail);
            phone = itemView.findViewById(R.id.tvPhone);
            activeTag = itemView.findViewById(R.id.tvActiveTag);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            btnSetActive = itemView.findViewById(R.id.btnSetActive);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
