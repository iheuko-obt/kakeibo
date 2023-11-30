package com.websarva.wings.android.sqlitekakeibo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyAdapter_E extends RecyclerView.Adapter<MyAdapter_E.ItemViewHolder>{
    ArrayList<statisticsActivity.DataPack> mDataset_E;
    statisticsActivity.DataPack DataPack;
    FragmentManager fragmentManager;
    public DBEntity entity;

    public int ID;


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

       
        public TextView date;
        public TextView tag;
        public TextView price;
        public TextView id;

        public ItemViewHolder(View v){
            super(v);
            date = (TextView)v.findViewById(R.id.date);
            tag = (TextView) v.findViewById(R.id.tag);
            price = (TextView) v.findViewById(R.id.price);
            id = (TextView)v.findViewById(R.id.id);
        }
    }

    public MyAdapter_E(ArrayList<statisticsActivity.DataPack> mDataset_E, FragmentManager fragmentManager){
        this.mDataset_E = mDataset_E;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recentadd_temp, parent, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // アイテムがクリックされたときの処理をここに書く
                TextView id = v.findViewById(R.id.id);
                ID = Integer.parseInt(id.getText().toString());
                DataPack = mDataset_E.get(ID);
                entity = DataPack.getRoomEntity();
                Bundle args = new Bundle();
                args.putInt("key",ID);
                DialogFragment dialog = new DataEditDialog();
                dialog.setArguments(args);
                dialog.show(fragmentManager,"EditDialog");
            }
        });


        return new ItemViewHolder(v);
    }

    //tempListToAddを独自のクラスに置き換えてentityそのものを保持させて、queryの引数に使いたい
    //list.add(new MyData("string1", "string2", "string3", new RoomEntity()));
    //そのクラスに↓で使う用のget○○メソッドもつける

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        statisticsActivity.DataPack data = mDataset_E.get(position);
        holder.date.setText(data.getDateToDisplay());
        holder.tag.setText(data.getTag());
        holder.price.setText(data.getEachPrice());
        holder.id.setText(Integer.toString(position));

        //Entity entity = data.getRoomEntity();
//        holder..setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        //Log.d("MyAdapter","itemCount は「" + mDataset.size() + "」");
        return mDataset_E.size();
    }


}








