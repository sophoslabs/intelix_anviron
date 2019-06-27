package com.sophos.anviron.models;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "file")
public class File {
    @PrimaryKey(autoGenerate = true)
    @NonNull private Long file_id;
    @NonNull private String file_path;

    @NonNull
    public Long getFile_id() {
        return file_id;
    }

    public void setFile_id(@NonNull Long file_id) {
        this.file_id = file_id;
    }

    @NonNull
    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(@NonNull String file_path) {
        this.file_path = file_path;
    }
}