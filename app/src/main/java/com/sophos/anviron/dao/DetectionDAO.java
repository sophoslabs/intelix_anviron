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

    @Query("SELECT f.file_id as fileId, f.file_path as filePath, d.detection_id as detectionId, d.detection_type as detectionType, d.detection_name as detectionName, d.detection_time as detectionTime, d.status as detectionStatus from file f JOIN detection d on f.file_id = d.file_id")
    public LiveData<List<FileDetectionMapping>> getFileDetections();

    @Query("SELECT count(d.detection_id) FROM detection d join file_scan_mapping m on d.file_id = m.file_id join scan s on s.scan_id = m.scan_id where s.type = :scanType")
    public Integer getTotalDetectionsByScanType(String scanType);
}
