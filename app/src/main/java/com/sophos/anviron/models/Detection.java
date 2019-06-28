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
    private String detection_type;
    private Long rep_score;
    private String detection_time;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetection_type() {
        return detection_type;
    }

    public void setDetection_type(String detection_type) {
        this.detection_type = detection_type;
    }

    public String getDetection_name() {
        return detection_name;
    }

    public void setDetection_name(String detection_name) {
        this.detection_name = detection_name;
    }

    public Long getRep_score() {
        return rep_score;
    }

    public void setRep_score(Long rep_score) {
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

    public String toString(){
        return "detection id: "+detection_id
                + " detection name: "+detection_name
                + " file id: "+file_id
                + " rep score: "+rep_score
                + " detection type: " + detection_type
                + " detection time: " + detection_time
                + " detection status: " + status;

    }
}
