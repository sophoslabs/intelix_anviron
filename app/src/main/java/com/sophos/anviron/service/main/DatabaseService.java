package com.sophos.anviron.service.main;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sophos.anviron.dao.*;
import com.sophos.anviron.models.*;

@Database(entities = {Scan.class, File.class, FileScanMapping.class, Detection.class},
            version = DatabaseService.DATABASE_VERSION)
public abstract class DatabaseService extends RoomDatabase {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "anviron_db";

    public abstract ScanDAO getScanDAO();
    public abstract FileDAO getFileDAO();
    public abstract FileScanMappingDAO getMappingDAO();
    public abstract DetectionDAO getDetectionDAO();

    private static DatabaseService mInstance;

    public static synchronized DatabaseService getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = Room.databaseBuilder(context, DatabaseService.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }



}
