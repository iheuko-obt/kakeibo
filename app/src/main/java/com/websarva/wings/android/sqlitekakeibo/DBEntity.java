package com.websarva.wings.android.sqlitekakeibo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dbentitys")
public class DBEntity {
    @PrimaryKey(autoGenerate = true)
    public int id; //表示しない

    public int getId() {
        return id;
    }

    public void setId(int id){this.id = id;}

    //表示する
    @ColumnInfo(name = "date_to_display")
    public String dateToDisplay;

    public String getDateToDisplay(){return dateToDisplay;}

    @ColumnInfo(name = "each_price")
    public String eachPrice;

    public String getEachPrice(){return eachPrice;}

    @ColumnInfo(name = "tag")
    public String tag;

    public String getTag(){return tag;}
    public void setTag(String newTag){
        tag = newTag;
    }

    //表示しない
    @ColumnInfo(name = "date_to_sort")
    public int dateToSort;

    @ColumnInfo(name = "month_to_sort")
    public int monthToSort;
    public int getMonthToSort(){return monthToSort;}

    @ColumnInfo(name = "year_to_sort")
    public int yearToSort;
    public int getYearToSort(){return yearToSort;}

    @ColumnInfo(name = "price_to_cul")
    public int priceToCul;
    public int getPriceToCul(){return priceToCul;}

    //entityのset()類を定義
    public DBEntity(String tempDate,String tag_selected,String inputPrice_st){
        String[] a = tempDate.split("/");
        this.dateToDisplay = tempDate;
        this.eachPrice = inputPrice_st;
        this.tag = tag_selected;
        this.yearToSort = Integer.parseInt(a[0]);
        this.monthToSort = Integer.parseInt(a[1]);
        this.dateToSort = Integer.parseInt(a[2]);
        this.priceToCul = Integer.parseInt(inputPrice_st);

    }

    public DBEntity() {

    }
}
