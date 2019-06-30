package com.sophos.anviron.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.v4.app.INotificationSideChannel;

import com.sophos.anviron.MainActivity;
import com.sophos.anviron.R;
import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.service.main.DatabaseService;
import java.util.List;

public class DatabaseRepository {

    private LiveData<List<DetectionDAO.FileDetectionMapping>> liveFileDetectionMappings;
    private LiveData<List<ScanDAO.ScanReport>> liveScanReportList;
    private List<String> filesScannedQuick;
    private List<String> filesScannedStatic;
    private List<String> filesScannedDynamic;
    private Integer totalScannedFilesQuick;
    private Integer totalScannedFilesDeep;
    private Integer totalDetectionsQuick;
    private Integer totalDetectionsDeep;
    private Double totalCostQuick;
    private Double totalCostDeep;

    public DatabaseRepository(Application application) {
        DatabaseService dbInstance = DatabaseService.getInstance(application.getApplicationContext());
        liveFileDetectionMappings = dbInstance.getDetectionDAO().getFileDetections();
        liveScanReportList = dbInstance.getScanDAO().getScanReport();

        totalDetectionsQuick = dbInstance.getDetectionDAO().getTotalDetectionsByScanType("quick");
        totalDetectionsDeep = dbInstance.getDetectionDAO().getTotalDetectionsByScanType("static") + dbInstance.getDetectionDAO().getTotalDetectionsByScanType("dynamic");

        filesScannedQuick = dbInstance.getFileDAO().getTotalFilesByScanType("quick");
        totalScannedFilesQuick = filesScannedQuick.size();

        filesScannedStatic = dbInstance.getFileDAO().getTotalFilesByScanType("static");
        Integer totalFilesStatic = filesScannedStatic.size();

        filesScannedDynamic = dbInstance.getFileDAO().getTotalFilesByScanType("dynamic");
        Integer totalFilesDynamic = filesScannedDynamic.size();

        Double quickCost = Double.parseDouble(application.getString(R.string.quick_cost_dollar));
        Double staticCost = Double.parseDouble(application.getString(R.string.static_cost_dollar));
        Double dynamicCost = Double.parseDouble(application.getString(R.string.dynamic_cost_dollar));
        totalScannedFilesDeep =  totalFilesStatic + totalFilesDynamic ;
        totalCostQuick = totalScannedFilesQuick*quickCost;
        totalCostDeep = totalFilesStatic*staticCost + totalFilesDynamic*dynamicCost;
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

    public LiveData<List<ScanDAO.ScanReport>> getLiveScanReportList() {
        return liveScanReportList;
    }

    public Integer getTotalDetectionsQuick() {
        return totalDetectionsQuick;
    }
    public Integer getTotalDetectionsDeep() {
        return totalDetectionsDeep;
    }

    public Double getTotalCostQuick() {
        return totalCostQuick;
    }

    public Double getTotalCostDeep() {
        return totalCostDeep;
    }

    public List<String> getFilesScannedQuick() {
        return filesScannedQuick;
    }

    public List<String> getFilesScannedStatic() {
        return filesScannedStatic;
    }

    public List<String> getFilesScannedDynamic() {
        return filesScannedDynamic;
    }
}
