package com.sophos.anviron.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.v4.app.INotificationSideChannel;

import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.service.main.DatabaseService;
import java.util.List;

public class DatabaseRepository {

    private LiveData<List<DetectionDAO.FileDetectionMapping>> liveFileDetectionMappings;
    private LiveData<List<ScanDAO.ScanReport>> liveScanReportList;
    private Integer totalScannedFilesQuick;
    private Integer totalScannedFilesDeep;
    private Integer totalDetectionsQuick;
    private Integer totalDetectionsDeep;

    public DatabaseRepository(Application application) {
        DatabaseService dbInstance = DatabaseService.getInstance(application.getApplicationContext());
        liveFileDetectionMappings = dbInstance.getDetectionDAO().getFileDetections();
        liveScanReportList = dbInstance.getScanDAO().getScanReport();
        totalScannedFilesQuick = dbInstance.getFileDAO().getTotalFilesByScanType("quick");
        totalScannedFilesDeep = dbInstance.getFileDAO().getTotalFilesByScanType("static") + dbInstance.getFileDAO().getTotalFilesByScanType("dynamic");
        totalDetectionsQuick = dbInstance.getDetectionDAO().getTotalDetectionsByScanType("quick");
        totalDetectionsDeep = dbInstance.getDetectionDAO().getTotalDetectionsByScanType("static") + dbInstance.getDetectionDAO().getTotalDetectionsByScanType("dynamic");
    }

    public Integer getTotalScannedFilesQuick() {
        return totalScannedFilesQuick;
    }

    public Integer getTotalScannedFilesDeep() {
        return totalScannedFilesDeep;
    }

    public LiveData<List<DetectionDAO.FileDetectionMapping>> getLiveFileDetectionMappings() {
        return liveFileDetectionMappings;
    }

    public LiveData<List<ScanDAO.ScanReport>> getScanReport() {
        return liveScanReportList;
    }

    public Integer getTotalDetectionsQuick() {
        return totalDetectionsQuick;
    }

    public Integer getTotalDetectionsDeep() {
        return totalDetectionsDeep;
    }
}
