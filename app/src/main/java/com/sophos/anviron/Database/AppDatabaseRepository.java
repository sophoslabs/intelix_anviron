package com.sophos.anviron.Database;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.sophos.anviron.AppDatabase;
import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.dao.FileDAO;
import com.sophos.anviron.dao.FileScanMappingDAO;
import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.models.Detection;
import com.sophos.anviron.models.File;
import com.sophos.anviron.models.FileScanMapping;
import com.sophos.anviron.models.Scan;

import java.util.List;
import java.util.ListIterator;

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
        AppDatabase db = AppDatabase.getInstance(application);
        scanDAO = db.getScanDAO();
        fileDAO = db.getFileDAO();
        fileScanMappingDAO = db.getMappingDAO();
        detectionDAO = db.getDetectionDAO();
    }

    public List<Scan> getAllScans() {
        return scanDAO.getScanInfo();
    }

    public Scan getScanById(Long id) {
        return scanDAO.getScansById(id);
    }


}
