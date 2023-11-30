package com.websarva.wings.android.sqlitekakeibo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemViewHolder>{
    ArrayList<ArrayList<String>> mDataset;


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView apartPrice;
        public TextView sumPrice;
        public TextView date;
        public TextView tag;
        public TextView price;

        public ItemViewHolder(View v){
            super(v);
            date = (TextView)v.findViewById(R.id.date);
            tag = (TextView) v.findViewById(R.id.tag);
            price = (TextView) v.findViewById(R.id.price);
        }
    }

    public MyAdapter(ArrayList<ArrayList<String>> mDataset){
        this.mDataset = mDataset;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recentadd_temp,
                        parent,
                        false);
        return new ItemViewHolder(v);
    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        ArrayList<String> data = mDataset.get(position);
        holder.date.setText(data.get(0));
        holder.tag.setText(data.get(1));
        holder.price.setText(data.get(2));
//        holder.mTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeFromDataset(data);
//            }
//        });
    }

    @Override
    public int getItemCount() {
      //  Log.d("MyAdapter","itemCount は「" + mDataset.size() + "」");
        return mDataset.size();
    }


}








