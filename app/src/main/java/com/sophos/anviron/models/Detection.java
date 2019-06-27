package com.sophos.anviron.models;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import java.sql.Timestamp;

@Entity(tableName = "detection")
public class Detection {
    @PrimaryKey
    @NonNull private String detection_id;
    @NonNull private String file_id;
    private String detection_name;
    private Integer rep_score;
    private String detection_time;

    public String getDetection_name() {
        return detection_name;
    }

    public void setDetection_name(String detection_name) {
        this.detection_name = detection_name;
    }

    public Integer getRep_score() {
        return rep_score;
    }

    public void setRep_score(Integer rep_score) {
        this.rep_score = rep_score;
    }

    public String getDetection_time() {
        return detection_time;
    }

    public void setDetection_time(String detection_time) {
        this.detection_time = detection_time;
    }

    @NonNull
    public String getDetection_id() {
        return detection_id;
    }

    public void setDetection_id(@NonNull String detection_id) {
        this.detection_id = detection_id;
    }

    @NonNull
    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(@NonNull String file_id) {
        this.file_id = file_id;
    }
}
