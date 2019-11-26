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