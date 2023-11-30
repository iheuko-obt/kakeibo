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

public class tagEdit_adapter extends RecyclerView.Adapter<tagEdit_adapter.ItemViewHolder>{

    private ArrayList<ArrayList<String>> tag_id;
    private ArrayList<String> oneSet;
    FragmentManager fragmentManager;
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tag;
        public TextView id;

        public ItemViewHolder(View v){
            super(v);
            id  = (TextView)v.findViewById(R.id.id);
            tag = (TextView) v.findViewById(R.id.tag);
        }
    }

    public tagEdit_adapter(ArrayList<ArrayList<String>> tag_id, FragmentManager fragmentManager){
        this.tag_id = tag_id;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.to_tagedit_adapter, parent, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // アイテムがクリックされたときの処理をここに書く
                TextView id = v.findViewById(R.id.id);
                String ID = id.getText().toString();
                TextView tag = v.findViewById(R.id.tag);
                String Tag = tag.getText().toString();
                Bundle args = new Bundle();
                String[] a = new String[2];
                a[0] = ID;
                a[1] = Tag;
                args.putStringArray("key", a);
                DialogFragment dialog = new tagEdit_dialog();
                dialog.setArguments(args);
                dialog.show(fragmentManager,"tagEditDialog");
            }
        });


        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        oneSet = tag_id.get(position);
        holder.id.setText(oneSet.get(0));
        holder.tag.setText(oneSet.get(1));
    }

    @Override
    public int getItemCount() {
        return tag_id.size();
    }
}
