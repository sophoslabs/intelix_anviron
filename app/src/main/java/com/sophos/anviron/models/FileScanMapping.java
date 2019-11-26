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

@Entity(tableName = "file_scan_mapping")
public class FileScanMapping {
    @PrimaryKey(autoGenerate = true)
    @NonNull private Long id;
    @NonNull private String file_id;
    @NonNull private String scan_id;
    @NonNull private String status;

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(@NonNull String file_id) {
        this.file_id = file_id;
    }

    @NonNull
    public String getScan_id() {
        return scan_id;
    }

    public void setScan_id(@NonNull String scan_id) {
        this.scan_id = scan_id;
    }

    @Override
    public String toString() {
        String str = "scan id: " + this.scan_id
                + " id: " + this.id
                + " file id: " + this.file_id
                + " status: " + this.status;
        return str;
    }
}
