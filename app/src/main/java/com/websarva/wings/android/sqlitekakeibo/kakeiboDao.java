package com.websarva.wings.android.sqlitekakeibo;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Dao;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface kakeiboDao {

    @Query("SELECT * FROM dbentitys")
    LiveData<List<DBEntity>> setList();

    @Query("SELECT * FROM dbentitys ORDER BY date_to_sort DESC")
    List<DBEntity> getList();

    @Query("SELECT * FROM dbentitys WHERE tag = :tag ORDER BY date_to_sort DESC")//tag引数にとって取得させる
    List<DBEntity> getApartList(String tag);

    @Insert
    Completable insert(DBEntity entity);

    @Update
    void updateAData(DBEntity entity);

    @Delete
    void deleteAData(DBEntity entity);
}
