package com.sophos.anviron.dao;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;
import com.sophos.anviron.models.FileScanMapping;


@Dao
public interface FileScanMappingDAO extends BaseDAO<FileScanMapping> {
    @Query("SELECT * FROM file_scan_mapping")
    public List<FileScanMapping> getMapping();

    @Query("Select * FROM file_scan_mapping WHERE scan_id = :scan_id AND file_id = :file_id")
    public List<FileScanMapping> getDetectionsById(Long scan_id, Long file_id);
}
