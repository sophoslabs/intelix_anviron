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
        public String total_remaining_files;
        public String scan_status;
    }

    @Query("SELECT * FROM scan")
    public List<Scan> getScanInfo();

    @Query("SELECT s.scan_id as scan_id," +
            "s.type as scan_type," +
            "s.is_file_uploaded as is_file_uploaded," +
            "s.submission_time as submission_time," +
            "s.completion_time as completion_time," +
            "sub1.total_files as total_files," +
            "sub2.in_progress_files as total_remaining_files,"+
            "case when sub2.in_progress_files>0 then 'in progress' else 'completed' end as scan_status "+
            "FROM scan s," +
            "(SELECT m.scan_id as scan_id, count(m.file_id) as total_files " +
            "FROM file_scan_mapping m " +
            "GROUP BY scan_id) as sub1, " +
            "(SELECT m.scan_id as scan_id, count(m.file_id) as in_progress_files " +
            "FROM file_scan_mapping m WHERE m.status='in progress' " +
            "GROUP BY scan_id) as sub2 " +
            "WHERE sub1.scan_id = s.scan_id "+
            "AND sub2.scan_id = s.scan_id"
    )
    public LiveData<List<ScanReport>> getScanReport();

    @Query("Select * FROM scan WHERE scan_id = :id")
    public Scan getScansById(String id);
}
