package com.websarva.wings.android.sqlitekakeibo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {tagEntity.class}, version = 1)
public abstract class tagDB extends RoomDatabase {
    private static tagDB instance;
    private static final Object LOCK = new Object();

    public static tagDB getDatabase(Context context){
        synchronized (LOCK) {
            if (instance == null){
                instance = Room.databaseBuilder(context.getApplicationContext(), tagDB.class,"da")
                        .fallbackToDestructiveMigration()
                        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                        .enableMultiInstanceInvalidation()
                        .build();
            }
        }

        return instance;
    }

    public abstract tagSpinnerDao tagSpinnerDao();
}
