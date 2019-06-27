package com.sophos.anviron.models;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "file_scan_mapping")
public class FileScanMapping {
    @PrimaryKey
    @NonNull private Integer id;
    @NonNull private Long file_id;
    @NonNull private Long scan_id;

    @NonNull
    public Long getFile_id() {
        return file_id;
    }

    public void setFile_id(@NonNull Long file_id) {
        this.file_id = file_id;
    }

    @NonNull
    public Long getScan_id() {
        return scan_id;
    }

    public void setScan_id(@NonNull Long scan_id) {
        this.scan_id = scan_id;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }
}
