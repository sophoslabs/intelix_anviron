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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;
import com.sophos.anviron.models.FileScanMapping;

@Dao
public interface FileScanMappingDAO extends BaseDAO<FileScanMapping> {

    static class CustomJoinFileScanMapping {

        public String fileId;
        public String filePath;
        public String scanId;
        public String scanType;
    }

    @Query("SELECT * FROM file_scan_mapping")
    public List<FileScanMapping> getMapping();

    @Query("Select * FROM file_scan_mapping WHERE scan_id = :scan_id AND file_id = :file_id")
    public List<FileScanMapping> getDetectionsById(Long scan_id, Long file_id);

    @Query("SELECT f.file_id as fileId, f.file_path as filePath, m.scan_id as scanId, s.type as scanType from file f JOIN file_scan_mapping m on f.file_id = m.file_id JOIN scan s on s.scan_id = m.scan_id WHERE m.status = :status")
    public List<CustomJoinFileScanMapping> getFilesByStatus(String status);

    @Query("SELECT f.file_id as fileId, f.file_path as filePath, m.scan_id as scanId, s.type as scanType from file f JOIN file_scan_mapping m on f.file_id = m.file_id JOIN scan s on s.scan_id = m.scan_id WHERE m.scan_id = :scan_id")
    public List<CustomJoinFileScanMapping> getFilesByScanId(String scan_id);

    @Query("UPDATE file_scan_mapping SET status = :status WHERE scan_id = :scan_id and file_id = :file_id")
    int updateStatus(String status, String scan_id, String file_id);

    @Query("UPDATE file_scan_mapping SET status = :status WHERE scan_id = :scan_id")
    int updateStatus(String status, String scan_id);

    @Query("SELECT CASE WHEN count(status) > 0 then 'in progress' else 'completed' end as status from file_scan_mapping where status = 'in progress' and scan_id =:id")
    public String getScanStatusByScanId(String id);

}
