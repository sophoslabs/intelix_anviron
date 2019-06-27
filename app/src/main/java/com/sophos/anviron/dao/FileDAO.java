package com.sophos.anviron.dao;
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

}
