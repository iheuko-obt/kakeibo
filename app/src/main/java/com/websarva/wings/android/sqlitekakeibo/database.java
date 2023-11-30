package com.websarva.wings.android.sqlitekakeibo;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DBEntity.class} , version = 1)
public abstract class database extends RoomDatabase {
    private static database instance;
    public static database getDatabase(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), database.class,"da")
                    .fallbackToDestructiveMigration()
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .enableMultiInstanceInvalidation()
                    .build();
        }

        return instance;
    }

    public abstract kakeiboDao kakeiboDao();
}
