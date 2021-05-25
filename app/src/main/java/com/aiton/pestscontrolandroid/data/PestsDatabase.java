package com.aiton.pestscontrolandroid.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.PestsDao;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.data.persistence.TrapDao;

@Database(entities = {Pests.class, Trap.class}, version = 9, exportSchema = false)
public abstract class PestsDatabase extends RoomDatabase {
    private static PestsDatabase INSTALL;

    public static synchronized PestsDatabase getInstance(Context context) {
        if ((INSTALL == null)) {
            INSTALL = Room.databaseBuilder(context.getApplicationContext(), PestsDatabase.class, "pests")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    //.addMigrations(MIGRATION_5_6)
                    .build();
        }
        return INSTALL;
    }

    public abstract PestsDao getPestsDao();
    public abstract TrapDao getTrapDao();
}
