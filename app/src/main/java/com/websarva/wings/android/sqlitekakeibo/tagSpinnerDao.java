package com.websarva.wings.android.sqlitekakeibo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface tagSpinnerDao {

    @Query("SELECT * FROM tagEntity")
    LiveData<List<tagEntity>> setListToSp();

    @Query("SELECT * FROM tagEntity")
    List<tagEntity> setListToTest();

    @Query("SELECT * FROM TAGENTITY WHERE id == :id")
    tagEntity getTagEntity(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(tagEntity tagEntity);

    @Delete
    Completable delete(tagEntity tagEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(tagEntity tagEntity);
}
