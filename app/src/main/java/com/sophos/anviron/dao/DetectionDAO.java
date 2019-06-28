package com.sophos.anviron.dao;
import java.util.List;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;
import com.sophos.anviron.models.Detection;

@Dao
public interface DetectionDAO extends BaseDAO<Detection> {
    @Query("SELECT * FROM detection")
    public List<Detection> getDetections();

    @Query("Select * FROM detection WHERE detection_id = :detection_id")
    public Detection getDetectionsById(Long detection_id);
}
