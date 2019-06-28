package com.sophos.anviron.Database;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.sophos.anviron.service.main.DatabaseService;
import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.dao.FileDAO;
import com.sophos.anviron.dao.FileScanMappingDAO;
import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.models.Detection;
import com.sophos.anviron.models.File;
import com.sophos.anviron.models.FileScanMapping;
import com.sophos.anviron.models.Scan;

import java.util.List;

public class AppDatabaseRepository {

    private ScanDAO scanDAO;
    private FileDAO fileDAO;
    private FileScanMappingDAO fileScanMappingDAO;
    private DetectionDAO detectionDAO;

    private LiveData<List<Scan>> allScanJobs;
    private LiveData<List<File>> allFiles;
    private LiveData<List<Detection>> allDetections;
    private LiveData<List<FileScanMapping>> allMappings;

    AppDatabaseRepository(Application application)
    {
        DatabaseService db = DatabaseService.getInstance(application);
        scanDAO = db.getScanDAO();
        fileDAO = db.getFileDAO();
        fileScanMappingDAO = db.getMappingDAO();
        detectionDAO = db.getDetectionDAO();
    }

    public List<Scan> getAllScans() {
        return scanDAO.getScanInfo();
    }

    public Scan getScanById(String id) {
        return scanDAO.getScansById(id);
    }


}
