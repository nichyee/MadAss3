package com.mad.assignment3.RecyclerViewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mad.assignment3.Models.Item;
import com.mad.assignment3.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends  RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    
    private ArrayList<Item> mItems;
    private Context mContext;

    public ItemAdapter(Context context, ArrayList<Item> list){
        this.mContext = context;
        this.mItems = list;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Item item = mItems.get(position);
        
        holder.itemName.setText(item.getName());
        holder.quantity.setText(String.valueOf(item.getAmount()));

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemName, quantity;
        
        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName_tv);
            quantity = itemView.findViewById(R.id.quantity_tv);
        }
    }
}
