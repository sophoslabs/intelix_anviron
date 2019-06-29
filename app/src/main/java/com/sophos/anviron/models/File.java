package com.sophos.anviron.models;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "file")
public class File {

    @PrimaryKey
    @NonNull private String file_id;
    @NonNull private String file_path;


    @NonNull
    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(@NonNull String file_id) {
        this.file_id = file_id;
    }

    @NonNull
    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(@NonNull String file_path) {
        this.file_path = file_path;
    }

    public String toString()
    {
        return "file_id: "+file_id +
                " file_path: "+file_path;
    }
}