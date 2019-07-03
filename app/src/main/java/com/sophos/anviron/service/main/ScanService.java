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
import com.sophos.anviron.ReportsActivity;
import com.sophos.anviron.dao.FileScanMappingDAO;
import com.sophos.anviron.models.Detection;
import com.sophos.anviron.util.main.APIWrapper;
import com.sophos.anviron.util.main.CommonUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ScanService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    private static String apiURI = "https://de.api.labs.sophos.com";
    private static String apiEndpoint = null;
    private static String corelationId = null;


    public ScanService() {
        super("ScanService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        DatabaseService dbInstance = DatabaseService.getInstance(getApplicationContext());

        try {

            Log.i("ScanService", "Running ScanService...");

            Log.i("report files: ", dbInstance.getFileDAO().getFiles().toString());
            Log.i("report filesMapping: ", dbInstance.getMappingDAO().getMapping().toString());

            while (true) {

                //Handle quick scan (without submission) requests
                List<FileScanMappingDAO.CustomJoinFileScanMapping> customJoinFileScanMappings = dbInstance.getMappingDAO().getFilesByStatus("in progress");

                try {
                    for (FileScanMappingDAO.CustomJoinFileScanMapping mapping : customJoinFileScanMappings) {

                        boolean hasScanResult = false;
                        Long reputationScore = null;

                        try {

                            String fileSHA256 = CommonUtils.getSHA256(mapping.filePath);
                            HashMap<String, String> params_map = new HashMap<>();
                            HashMap<String, String> fileOrJobId = new HashMap<>();
                            JSONParser parser = new JSONParser();


                            if (mapping.scanType.equalsIgnoreCase("quick")) {
                                fileOrJobId.put("sha256", fileSHA256);
                                apiEndpoint = "lookup/files/v1";
                            } else if (mapping.scanType.equalsIgnoreCase("static")) {
                                fileOrJobId.put("file", mapping.filePath);
                                apiEndpoint = "analysis/file/static/v1";
                            } else if (mapping.scanType.equalsIgnoreCase("dynamic")) {
                                fileOrJobId.put("file", mapping.filePath);
                                apiEndpoint = "analysis/file/dynamic/v1";
                            }


                            APIWrapper api_wrapper = new APIWrapper(apiURI, apiEndpoint, corelationId, params_map, fileOrJobId);
                            HashMap<String, String> report_map = api_wrapper.get_file_report();

                            if (mapping.scanType.equalsIgnoreCase("quick")) {

                                Log.i("report_map_quick", report_map.toString());

                                String response = report_map.get(fileSHA256);
                                reputationScore = Long.parseLong("-1");

                                if (!response.equalsIgnoreCase("NA")) {
                                    JSONObject responseJson = (JSONObject) parser.parse(response);
                                    reputationScore = (Long) responseJson.get("reputationScore");
                                }

                                hasScanResult = true;


                            } else if (mapping.scanType.equalsIgnoreCase("static")) {

                                Log.i("report map static", report_map.toString());
                                String responseStr = (String) report_map.get(mapping.filePath);
                                JSONObject responseJson = null;

                                if (!responseStr.equalsIgnoreCase("NA")) {
                                    responseJson = (JSONObject) parser.parse(responseStr);

                                    String jobStatus = (String) responseJson.getOrDefault("jobStatus", "ERROR");

                                    if (jobStatus.trim().equalsIgnoreCase("SUCCESS")) {
                                        hasScanResult = true;
                                        try {
                                            reputationScore = (Long) ((JSONObject) ((JSONObject) responseJson.get("report")).get("reputation")).get("score");
                                        } catch (Exception e) {
                                            reputationScore = Long.parseLong("-1");
                                            hasScanResult = true;
                                        }
                                    } else if (jobStatus.trim().equalsIgnoreCase("ERROR")) {
                                        reputationScore = Long.parseLong("-1");
                                        hasScanResult = true;
                                    }
                                } else {
                                    reputationScore = Long.parseLong("-1");
                                    hasScanResult = true;
                                }

                            } else if (mapping.scanType.equalsIgnoreCase("dynamic")) {

                                Log.i("report map dynamic", report_map.toString());
                                String responseStr = (String) report_map.get(mapping.filePath);
                                JSONObject responseJson = null;

                                if (!responseStr.equalsIgnoreCase("NA")) {
                                    responseJson = (JSONObject) parser.parse(responseStr);

                                    String jobStatus = (String) responseJson.getOrDefault("jobStatus", "ERROR");

                                    if (jobStatus.trim().equalsIgnoreCase("SUCCESS")) {
                                        hasScanResult = true;
                                        try {
                                            reputationScore = (Long) ((JSONObject) responseJson.get("report")).get("score");
                                        } catch (Exception e) {
                                            reputationScore = Long.parseLong("-1");
                                            hasScanResult = true;
                                        }
                                    } else if (jobStatus.trim().equalsIgnoreCase("ERROR")) {
                                        reputationScore = Long.parseLong("-1");
                                        hasScanResult = true;
                                    }
                                } else {
                                    reputationScore = Long.parseLong("-1");
                                    hasScanResult = true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            reputationScore = Long.parseLong("-1");
                            hasScanResult = true;

                        } catch (Error e) {
                            e.printStackTrace();
                            reputationScore = Long.parseLong("-1");
                            hasScanResult = true;
                        }

                        if (hasScanResult) {

                            String detectionType = CommonUtils.getDetectionType(reputationScore);

                            if (detectionType.equalsIgnoreCase("malware") || detectionType.equalsIgnoreCase("pua")) {

                                String detectionId = dbInstance.getDetectionDAO().getDetectionsByFileId(mapping.fileId);

                                Detection detection = new Detection();

                                detection.setDetection_name(detectionType);
                                detection.setDetection_type(detectionType);
                                detection.setFile_id(mapping.fileId);
                                detection.setRep_score(reputationScore);
                                detection.setStatus("detected");
                                detection.setDetection_time(CommonUtils.getCurrentDateTime());

                                //Add or update detection table
                                if (detectionId == null) {
                                    detection.setDetection_id(CommonUtils.generateUUID());
                                    dbInstance.getDetectionDAO().insert(detection);
                                } else {

                                    detection.setDetection_id(detectionId);
                                    String detectionStatus = dbInstance.getDetectionDAO().getDetectionStatusByDetectionId(detectionId);
                                    if(!detectionStatus.equalsIgnoreCase("clean")){
                                        dbInstance.getDetectionDAO().update(detection);
                                    }
                                }

                                Log.i("report quick/static/dynamic detection: ", detection.toString());
                            }
                            else{
                                String detectionId = dbInstance.getDetectionDAO().getDetectionsByFileId(mapping.fileId);
                                if (detectionId != null) {
                                    dbInstance.getDetectionDAO().deleteDetectionByDetectionId(detectionId);
                                }
                            }

                            dbInstance.getMappingDAO().updateStatus("completed", mapping.scanId, mapping.fileId);
                            //Add or update scan table for completion time
                            if (dbInstance.getMappingDAO().getScanStatusByScanId(mapping.scanId).equalsIgnoreCase("completed")) {
                                dbInstance.getScanDAO().updateCompletionTimeByScanId(CommonUtils.getCurrentDateTime(), mapping.scanId);
                                sendNotification(mapping);
                            }
                        }
                    }

                    Log.i("report detection: ", dbInstance.getDetectionDAO().getDetections().toString());
                    Log.i("report scans: ", dbInstance.getScanDAO().getScanInfo().toString());
                    Log.i("report files: ", dbInstance.getFileDAO().getFiles().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Thread.sleep(Integer.parseInt(getApplication().getResources().getString(R.string.poll_interval)));
            }

        } catch (Exception e) {
            Log.e("ScanService", e.toString());
        }
    }

    protected void sendNotification(FileScanMappingDAO.CustomJoinFileScanMapping mapping) {
        int randomInt = new Random().nextInt();
        createNotificationChannel();
        Intent notificationIntent = new Intent(getApplicationContext(), ReportsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), randomInt, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Anviron Security")
                .setContentText("Your report for " + mapping.scanType + " scan is ready. Click here to view.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(randomInt, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel 1";
            String description = "First channel for AnViron app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
