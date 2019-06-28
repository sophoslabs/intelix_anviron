package com.sophos.anviron.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.database.DatabaseRepository;

import java.util.List;

public class ReportViewModel extends AndroidViewModel {

    private DatabaseRepository databaseRepository;
    private LiveData<List<ScanDAO.ScanReport>> liveScanReportList;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        databaseRepository = new DatabaseRepository(application);
        liveScanReportList = databaseRepository.getScanReport();
    }

    public LiveData<List<ScanDAO.ScanReport>> getScanReport() {
        return liveScanReportList;
    }
}
