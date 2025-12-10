package com.mdu.jobinvoicing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

        // Access fields directly
        holder.fullName.setText(operator.name);
        holder.role.setText(operator.role);
        holder.email.setText("✉ " + operator.email);
        holder.phone.setText("📞 " + operator.phone);

        //holder.activeTag.setText(operator.isActive ? "Active" : "Inactive");

        holder.btnSetActive.setOnClickListener(v -> listener.onSetActive(operator));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(operator));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(operator));
    }

    @Override
    public int getItemCount() {
        return operatorList.size();
    }

    public static class OperatorViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, role, email, phone, activeTag;
        Button btnSetActive, btnEdit;
        ImageButton btnDelete;

        public OperatorViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.tvName);
            role = itemView.findViewById(R.id.tvRole);
            email = itemView.findViewById(R.id.tvEmail);
            phone = itemView.findViewById(R.id.tvPhone);
//            activeTag = itemView.findViewById(R.id.tvActiveTag); // make sure this TextView exists in layout
            btnSetActive = itemView.findViewById(R.id.btnSetActive);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

}
