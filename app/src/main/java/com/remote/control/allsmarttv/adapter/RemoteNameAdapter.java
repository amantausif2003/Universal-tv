package com.remote.control.allsmarttv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.models.RemoteNameModel;

import java.util.ArrayList;

public class RemoteNameAdapter extends RecyclerView.Adapter<RemoteNameAdapter.RemoteViewHolder> {

    private ArrayList<RemoteNameModel> remoteModelArrayList;
    private RemoteClickListener listener;

    public RemoteNameAdapter(ArrayList<RemoteNameModel> designModelArrayList, RemoteClickListener listener) {
        this.remoteModelArrayList = designModelArrayList;
        this.listener = listener;
    }

    public interface RemoteClickListener {
        void remoteItemClick(RemoteNameModel designModel, int position);
    }

    @NonNull
    @Override
    public RemoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.remote_item_name, parent, false);
        return new RemoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemoteViewHolder holder, int position) {
        RemoteNameModel remoteNameModel = remoteModelArrayList.get(position);
        holder.remoteItemName.setText(remoteNameModel.getRemoteName());
        holder.itemView.setOnClickListener(v -> {
            listener.remoteItemClick(remoteNameModel, position);
        });
    }

    @Override
    public int getItemCount() {
        return remoteModelArrayList.size();
    }

    public class RemoteViewHolder extends RecyclerView.ViewHolder {
        private TextView remoteItemName;

        public RemoteViewHolder(@NonNull View itemView) {
            super(itemView);
            remoteItemName = itemView.findViewById(R.id.remote_name);
        }
    }
}
