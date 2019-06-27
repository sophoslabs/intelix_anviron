package com.sophos.anviron;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sophos.anviron.dao.*;
import com.sophos.anviron.models.*;

@Database(entities = {Scan.class, File.class, FileScanMapping.class, Detection.class},
            version = AppDatabase.DATABASE_VERSION)
public abstract class AppDatabase extends RoomDatabase {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "anviron_db";

    public abstract ScanDAO getScanDAO();
    public abstract FileDAO getFileDAO();
    public abstract FileScanMappingDAO getMappingDAO();
    public abstract DetectionDAO getDetectionDAO();

    private static AppDatabase mInstance;
    public static AppDatabase getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }
}
