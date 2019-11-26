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
import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;
import com.sophos.anviron.models.Detection;

@Dao
public interface DetectionDAO extends BaseDAO<Detection> {

    static class FileDetectionMapping {

        public String fileId;
        public String filePath;
        public String detectionId;
        public String detectionType;
        public String detectionName;
        public String detectionTime;
        public String detectionStatus;
    }
    
    @Query("SELECT * FROM detection")
    public List<Detection> getDetections();

    @Query("Select detection_id FROM detection WHERE file_id= :fileId limit 1")
    public String getDetectionsByFileId(String fileId);

    @Query("Select status FROM detection WHERE detection_id= :detectionId limit 1")
    public String getDetectionStatusByDetectionId(String detectionId);

    @Query("SELECT f.file_id as fileId, f.file_path as filePath, d.detection_id as detectionId, d.detection_type as detectionType, d.detection_name as detectionName, d.detection_time as detectionTime, d.status as detectionStatus from file f JOIN detection d on f.file_id = d.file_id order by d.detection_time desc")
    public LiveData<List<FileDetectionMapping>> getFileDetections();

    @Query("SELECT count(distinct(d.detection_id)) FROM detection d join file_scan_mapping m on d.file_id = m.file_id join scan s on s.scan_id = m.scan_id where s.type = :scanType and d.status='detected' AND lower(m.status) NOT IN ('waiting for payment', 'payment failed')")
    public Integer getTotalDetectionsByScanType(String scanType);

    @Query("SELECT count(d.detection_id) as total_detections FROM detection d JOIN file_scan_mapping m on d.file_id = m.file_id where m.scan_id = :scanId and d.status!='clean' and d.status!='quarantine'")
    public Integer getDetectionsByScanId(String scanId);

    @Query("UPDATE detection set status=:status where detection_id=:detectionId")
    public int updateStatusByDetectionId(String status, String detectionId);

    @Query("DELETE FROM detection where detection_id=:detectionId")
    public int deleteDetectionByDetectionId(String detectionId);
}
