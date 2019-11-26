/*
 * #  Copyright (c) 2019. Sophos Limited
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 */

package com.sophos.anviron.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.sql.Timestamp;

@Entity(tableName = "scan")
public class Scan {
    @PrimaryKey
    @NonNull
    private String scan_id;
    private String type;
    private Boolean is_file_uploaded;
    private String submission_time;
    private String completion_time;

    @NonNull
    public String getScan_id() {
        return scan_id;
    }

    public void setScan_id(@NonNull String scan_id) {
        this.scan_id = scan_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIs_file_uploaded() {
        return is_file_uploaded;
    }

    public void setIs_file_uploaded(Boolean is_file_uploaded) {
        this.is_file_uploaded = is_file_uploaded;
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

    public String toString() {
        super.toString();
        String str = "Scan id: " + this.scan_id
                + " Scan type: " + this.type
                + " Submission time: " + this.submission_time
                + " Completion Time: " + this.completion_time;
        return str;
    }
}
