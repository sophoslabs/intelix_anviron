package com.sophos.anviron.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.service.main.DatabaseService;
import java.util.List;
import java.util.ListIterator;

public class DatabaseRepository {

    private LiveData<List<DetectionDAO.FileDetectionMapping>> liveFileDetectionMappings;
    private LiveData<List<ScanDAO.ScanReport>> liveScanReportList;

    public DatabaseRepository(Application application) {
        DatabaseService dbInstance = DatabaseService.getInstance(application.getApplicationContext());
        liveFileDetectionMappings = dbInstance.getDetectionDAO().getFileDetections();
        liveScanReportList = dbInstance.getScanDAO().getScanReport();
    }

    public LiveData<List<DetectionDAO.FileDetectionMapping>> getLiveFileDetectionMappings() {
        return liveFileDetectionMappings;
    }

    public LiveData<List<ScanDAO.ScanReport>> getScanReport() {
        return liveScanReportList;
    }
}
