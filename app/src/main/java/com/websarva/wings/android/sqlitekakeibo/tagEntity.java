package com.websarva.wings.android.sqlitekakeibo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"tag_to_sp"}, unique = true)})
public class tagEntity {
    @PrimaryKey(autoGenerate = true)
    public int id; //表示しない

    public int getId(){return id;}

    @ColumnInfo(name = "tag_to_sp")
    public  String tagToSpinner;

    public  String getTag(){
        return tagToSpinner;
    }

    public void setTag(String newTag){
        tagToSpinner = newTag;
    }

    public tagEntity(){
    }

    public tagEntity(String tagFromDialog){
        this.tagToSpinner = tagFromDialog;
    }

}
