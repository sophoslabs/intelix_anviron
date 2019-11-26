/*
 * #  Copyright (c) 2019. Sophos Limited
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 */

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
        liveScanReportList = databaseRepository.getLiveScanReportList();
    }

    public LiveData<List<ScanDAO.ScanReport>> getScanReport() {
        return liveScanReportList;
    }
}
