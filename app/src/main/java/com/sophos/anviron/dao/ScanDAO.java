package com.sophos.anviron.dao;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
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

    @Query("SELECT s.scan_id as scan_id," +
            "s.type as scan_type," +
            "s.is_file_uploaded as is_file_uploaded," +
            "s.submission_time as submission_time," +
            "s.completion_time as completion_time," +
            "sub.total_files as total_files " +
            "FROM scan s," +
            "(SELECT m.scan_id as scan_id, count(m.file_id) as total_files " +
            "FROM file_scan_mapping m " +
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
