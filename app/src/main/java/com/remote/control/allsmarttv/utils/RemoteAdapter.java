package com.remote.control.allsmarttv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remote.control.allsmarttv.Activities.LgWifiRemoteActivity;
import com.remote.control.allsmarttv.Activities.RemoteActivity;
import com.remote.control.allsmarttv.Activities.RokuPair;
import com.remote.control.allsmarttv.R;

import java.util.ArrayList;
import java.util.List;

public class RemoteAdapter extends RecyclerView.Adapter<RemoteAdapter.RemoteViewDetails> implements Filterable {

    private List<Remote_model> remote_models;
    private List<Remote_model> filterList;
    Context mContext;
    Activity mActivity;

    public RemoteAdapter(Context context, Activity activity) {
        mActivity = activity;
        mContext = context;
    }

    public void setList(List arrayList) {
        this.remote_models = arrayList;
        filterList = new ArrayList<>(arrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RemoteViewDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_item, parent, false);
        return new RemoteViewDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemoteViewDetails holder, int position) {

        holder.remote_name.setText(remote_models.get(position).getName());

        if (holder.remote_name.getText().toString().contains("Android")) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(mContext, RemoteActivity.class);
                    mContext.startActivity(intent);


                }
            });
        } else if (holder.remote_name.getText().toString().contains("Roku")) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, RokuPair.class);
                    mContext.startActivity(intent);
                }
            });
        } else if (holder.remote_name.getText().toString().contains("LG")) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, LgWifiRemoteActivity.class);
                    intent.putExtra("tv", "lg");
                    mContext.startActivity(intent);

                }
            });
        }

    }


    @Override
    public int getItemCount() {

        return remote_models.size();
    }

    @Override
    public Filter getFilter() {
        return listfilter;
    }

    private Filter listfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Remote_model> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Remote_model item : filterList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            remote_models.clear();
            remote_models.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };


    public class RemoteViewDetails extends RecyclerView.ViewHolder {

        private TextView remote_name;

        public RemoteViewDetails(@NonNull View itemView) {
            super(itemView);

            remote_name = itemView.findViewById(R.id.tv_name);

        }
    }

}
