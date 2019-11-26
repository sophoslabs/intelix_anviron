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

package com.sophos.anviron.dao;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;

import com.sophos.anviron.models.File;

@Dao
public interface FileDAO extends BaseDAO<File> {
    @Query("SELECT * FROM file")
    public List<File> getFiles();

    @Query("Select * FROM file WHERE file_id = :id")
    public List<File> getDetectionsById(Long id);

    @Query("Select file_id FROM file WHERE file_path=:filePath")
    public String getFileIdByFilePath(String filePath);

    @Query("Select f.file_path as files from file f join file_scan_mapping m on f.file_id = m.file_id join scan s on s.scan_id = m.scan_id where s.type = :scanType and lower(m.status) NOT IN ('waiting for payment', 'payment failed')")
    public List<String> getTotalFilesByScanType(String scanType);
}
