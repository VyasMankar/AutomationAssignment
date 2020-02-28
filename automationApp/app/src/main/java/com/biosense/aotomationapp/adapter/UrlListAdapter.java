package com.biosense.aotomationapp.adapter;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.biosense.aotomationapp.R;
import com.biosense.aotomationapp.model.url_Info_model;

import java.util.List;

public class UrlListAdapter extends RecyclerView.Adapter<UrlListAdapter.ViewHolder>{
    private List<url_Info_model> listdata;

    // RecyclerView recyclerView;
    public UrlListAdapter(List<url_Info_model> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final url_Info_model myListData = listdata.get(position);
        holder.textViewUrl.setText(listdata.get(position).getBaseurl()+""+listdata.get(position).getNode());
        holder.textViewDate.setText(listdata.get(position).getDate());
        holder.textViewTime.setText(listdata.get(position).getTime());
        holder.textUrlStatus.setText(listdata.get(position).getStatus());

        if(!listdata.get(position).getStatus().equals("pending"))
        {
            holder.mCardView.setCardBackgroundColor(Color.GREEN);
        }

    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewUrl,textViewDate,textViewTime,textUrlStatus;
        public CardView mCardView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewUrl = (TextView) itemView.findViewById(R.id.txtUrl);
            this.textViewDate = (TextView) itemView.findViewById(R.id.txtDate);
            this.textViewTime = (TextView) itemView.findViewById(R.id.txtTime);
            this.mCardView = (CardView) itemView.findViewById(R.id.cardOfURLList);
            this.textUrlStatus = (TextView) itemView.findViewById(R.id.urlStatus);
        }
    }
}