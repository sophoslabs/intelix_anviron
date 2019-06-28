package com.sophos.anviron.dao;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;
import com.sophos.anviron.models.Scan;

@Dao
public interface ScanDAO extends BaseDAO<Scan> {
    @Query("SELECT * FROM scan")
    public List<Scan> getScanInfo();

    @Query("Select * FROM scan WHERE scan_id = :id")
    public Scan getScansById(String id);
}
