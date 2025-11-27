package com.mdu.jobinvoicing;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyListAdapter extends ArrayAdapter<String> {
    private final Activity context;

    private final String[] id;
    private final String[] cost;
    private final String[] title;
    private final String[] status;

    public MyListAdapter(Activity context, String[] id, String[] cost, String[] title, String[] status) {
        super(context, R.layout.list_item_layout, id);
        this.context = context;
        this.id = id;
        this.cost = cost;
        this.title = title;
        this.status = status;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item_layout, null, true);

        TextView idText = rowView.findViewById(R.id.job_id);
        TextView costText = rowView.findViewById(R.id.job_cost);
        TextView titleText = rowView.findViewById(R.id.job_title);
        TextView statusText = rowView.findViewById(R.id.job_status);

        idText.setText(id[position]);
        costText.setText(cost[position]);
        titleText.setText(title[position]);
        statusText.setText(status[position]);

        if(status[position].equalsIgnoreCase("Closed")){
            statusText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jobbs_list_status_closed, 0, 0, 0);
            statusText.setCompoundDrawablePadding(8);
        } else if (status[position].equalsIgnoreCase("In Progress")) {
            statusText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jobbs_list_status_in_progress, 0, 0, 0);
            statusText.setCompoundDrawablePadding(8);
        }

        return rowView;
    }
}
