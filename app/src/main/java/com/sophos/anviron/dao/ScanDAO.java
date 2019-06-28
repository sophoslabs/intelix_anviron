package com.sophos.anviron.dao;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;
import com.sophos.anviron.models.Scan;

@Dao
public interface ScanDAO extends BaseDAO<Scan> {

    class ScanReport {
        public String scan_id;
        public String type;
        public Boolean is_file_uploaded;
        public String submission_time;
        public String completion_time;
        public Integer total_files;
        public String status;

        public ScanReport(String scan_id, String type, Boolean is_file_uploaded, String submission_time, String completion_time, Integer total_files, String status) {
            this.scan_id = scan_id;
            this.type = type;
            this.is_file_uploaded = is_file_uploaded;
            this.submission_time = submission_time;
            this.completion_time = completion_time;
            this.total_files = total_files;
            this.status = status;
        }
    }


    @Query("SELECT * FROM scan")
    public List<Scan> getScanInfo();

    @Query("Select s.scan_id as scan_id, " +
            "s.type as type, " +
            "s.is_file_uploaded as is_file_uploaded, " +
            "s.submission_time as submission_time," +
            "s.completion_time as completion_time," +
            "count(m.file_id) as total_files, " +
            "case when sub.count > 0 then 'in progress' else 'completed' end as status " +
            "FROM Scan s join file_scan_mapping m on s.scan_id = m.scan_id join " +
            "(Select m1.scan_id as scan_id, count(m1.file_id) as count " +
            "from file_scan_mapping m1 " +
            "where m1.status = 'in progress' " +
            "group by m1.scan_id) as sub on sub.scan_id = m.scan_id " +
            "group by s.scan_id")
    public LiveData<List<ScanReport>> getScanReport();

    @Query("Select * FROM scan WHERE scan_id = :id")
    public Scan getScansById(String id);
}
