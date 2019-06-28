package com.sophos.anviron.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.service.main.DatabaseService;
import java.util.List;

public class DatabaseRepository {

    private LiveData<List<DetectionDAO.FileDetectionMapping>> liveFileDetectionMappings;

    public DatabaseRepository(Application application) {
        DatabaseService dbInstance = DatabaseService.getInstance(application.getApplicationContext());
        liveFileDetectionMappings = dbInstance.getDetectionDAO().getFileDetections();
    }

    public LiveData<List<DetectionDAO.FileDetectionMapping>> getLiveFileDetectionMappings() {
        return liveFileDetectionMappings;
    }
}
