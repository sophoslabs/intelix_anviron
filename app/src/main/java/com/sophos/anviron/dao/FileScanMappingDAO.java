package com.sophos.anviron.dao;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.HashMap;
import java.util.List;

import com.sophos.anviron.models.File;
import com.sophos.anviron.models.FileScanMapping;


@Dao
public interface FileScanMappingDAO extends BaseDAO<FileScanMapping> {

    static class CustomJoinFileScanMapping {
        public String fileId;
        public String filePath;
        public String scanId;
    }

    @Query("SELECT * FROM file_scan_mapping")
    public List<FileScanMapping> getMapping();

    @Query("Select * FROM file_scan_mapping WHERE scan_id = :scan_id AND file_id = :file_id")
    public List<FileScanMapping> getDetectionsById(Long scan_id, Long file_id);

    @Query("SELECT f.file_id as fileId, f.file_path as filePath, m.scan_id as scanId from file f JOIN file_scan_mapping m on f.file_id = m.file_id WHERE m.status = :status")
    public List<CustomJoinFileScanMapping> getFilesByStatus(String status);
}
