package com.sophos.anviron.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.sql.Timestamp;

@Entity(tableName = "scan")
public class Scan {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long scan_id;
    private Boolean type;
    private String submission_time;
    private String completion_time;
    private Integer status;

    @NonNull
    public Long getScan_id() {
        return scan_id;
    }

    public void setScan_id(@NonNull Long scan_id) {
        this.scan_id = scan_id;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public String getSubmission_time() {
        return submission_time;
    }

    public void setSubmission_time(String submission_time) {
        this.submission_time = submission_time;
    }

    public String getCompletion_time() {
        return completion_time;
    }

    public void setCompletion_time(String completion_time) {
        this.completion_time = completion_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String toString() {
        String str = "Scan id: " + this.scan_id
                + " Scan type: " + this.type
                + " Submission time: " + this.submission_time
                + " Completion Time: " + this.completion_time
                + " Status " + this.status;
        return str;
    }
}
