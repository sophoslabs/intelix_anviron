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


    class FileDetectionMapping {

        public String fileId;
        public String filePath;
        public String detectionId;
        public String detectionType;
        public String detectionName;
        public String detectionTime;
        public String detectionStatus;

        public FileDetectionMapping(String fileId, String filePath, String detectionId, String detectionType, String detectionName, String detectionTime, String detectionStatus){
            this.fileId = fileId;
            this.filePath = filePath;
            this.detectionId = detectionId;
            this.detectionType = detectionType;
            this.detectionName = detectionName;
            this.detectionTime = detectionTime;
            this.detectionStatus = detectionStatus;
        }
    }


    @Query("SELECT * FROM detection")
    public List<Detection> getDetections();

    @Query("Select detection_id FROM detection WHERE file_id= :fileId limit 1")
    public String getDetectionsByFileId(String fileId);

    @Query("SELECT f.file_id as fileId, f.file_path as filePath, d.detection_id as detectionId, d.detection_type as detectionType, d.detection_name as detectionName, d.detection_time as detectionTime, d.status as detectionStatus from file f JOIN detection d on f.file_id = d.file_id")
    public LiveData<List<FileDetectionMapping>> getFileDetections();

}
