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

package com.sophos.anviron.dao;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import java.util.List;
import com.sophos.anviron.models.Scan;

@Dao
public interface ScanDAO extends BaseDAO<Scan> {

    static class ScanReport {
        public String scan_id;
        public String scan_type;
        public Boolean is_file_uploaded;
        public String submission_time;
        public String completion_time;
        public String total_files;
        public String scan_status;
    }

    @Query("SELECT * FROM scan")
    public List<Scan> getScanInfo();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Ignore
    @Query("SELECT s.scan_id as scan_id," +
            "s.type as scan_type," +
            "s.is_file_uploaded as is_file_uploaded," +
            "s.submission_time as submission_time," +
            "s.completion_time as completion_time," +
            "sub.total_files as total_files " +
            "FROM scan s," +
            "(SELECT m.scan_id as scan_id, count(m.file_id) as total_files " +
            "FROM file_scan_mapping m WHERE lower(m.status) NOT IN ('waiting for payment', 'payment failed') " +
            "GROUP BY scan_id) as sub " +
            "WHERE sub.scan_id = s.scan_id " +
            "ORDER BY s.submission_time desc"
    )
    public LiveData<List<ScanReport>> getScanReport();

    @Query("SELECT * FROM scan WHERE scan_id = :id")
    public Scan getScansById(String id);

    @Query("UPDATE scan SET completion_time = :completion_time WHERE scan_id = :scan_id")
    public int updateCompletionTimeByScanId(String completion_time, String scan_id);
}
