package com.sophos.anviron.service.main;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.sophos.anviron.R;
import com.sophos.anviron.dao.FileScanMappingDAO;
import com.sophos.anviron.models.File;
import com.sophos.anviron.models.FileScanMapping;
import com.sophos.anviron.models.Scan;
import com.sophos.anviron.util.main.APIWrapper;
import com.sophos.anviron.util.main.CommonUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScanService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    private static String apiURI = "https://de.api.labs.sophos.com";
    private static String apiEndpoint = "lookup/files/v1";
    private static String corelationId = null;

    public ScanService() {
        super("ScanService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Log.i("ScanService", "Running ScanService...");

            while (true) {

                DatabaseService dbInstance = DatabaseService.getInstance(getApplicationContext());

                //Handle quick scan (without submission) requests
                List<FileScanMappingDAO.CustomJoinFileScanMapping> customJoinFileScanMappings = dbInstance.getMappingDAO().getFilesByStatus("in progress");

                try {
                    for (FileScanMappingDAO.CustomJoinFileScanMapping mapping: customJoinFileScanMappings) {

                        String fileSHA256 = CommonUtils.getSHA256(mapping.filePath);
                        HashMap<String, String> params_map = new HashMap<>();
                        HashMap<String, String> fileOrJobId = new HashMap<>();
                        fileOrJobId.put("sha256", fileSHA256);
                        APIWrapper api_wrapper = new APIWrapper(apiURI, apiEndpoint, corelationId, params_map, fileOrJobId);
                        HashMap<String, String> report_map = api_wrapper.get_file_report();
                        Log.i("reportfile",mapping.fileId);
                        Log.i("reportscan",mapping.scanId);
                        Log.i("reportpath",mapping.filePath);
                        Log.i("report",report_map.get(fileSHA256));


                        dbInstance.getMappingDAO().update();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Thread.sleep(Integer.parseInt(getApplication().getResources().getString(R.string.poll_interval)));

            }

        } catch (Exception e) {
            Log.e("ScanService", e.toString());
        }
    }
}
