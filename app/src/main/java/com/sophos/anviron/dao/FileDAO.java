package com.sophos.anviron.dao;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;

import com.sophos.anviron.models.File;

@Dao
public interface FileDAO extends BaseDAO<File> {
    @Query("SELECT * FROM file")
    public List<File> getFiles();

    @Query("Select * FROM file WHERE file_id = :id")
    public List<File> getDetectionsById(Long id);

    @Query("Select file_id FROM file WHERE file_path=:filePath")
    public String getFileIdByFilePath(String filePath);

    @Query("Select f.file_path as files from file f join file_scan_mapping m on f.file_id = m.file_id join scan s on s.scan_id = m.scan_id where s.type = :scanTypes")
    public List<String> getTotalFilesByScanType(String scanTypes);
}
