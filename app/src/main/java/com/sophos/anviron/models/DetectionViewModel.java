package com.sophos.anviron.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.database.DatabaseRepository;

import java.util.List;

public class DetectionViewModel extends AndroidViewModel {

    private DatabaseRepository databaseRepository;
    private LiveData<List<DetectionDAO.FileDetectionMapping>> liveFileDetectionMappings;

    public DetectionViewModel(Application application) {

        super(application);

        databaseRepository = new DatabaseRepository(application);
        liveFileDetectionMappings = databaseRepository.getLiveFileDetectionMappings();
    }

    public LiveData<List<DetectionDAO.FileDetectionMapping>> getLiveFileDetectionMappings() {
        return liveFileDetectionMappings;
    }
}