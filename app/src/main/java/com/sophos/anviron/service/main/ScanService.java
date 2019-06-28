package com.sophos.anviron.service.main;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sophos.anviron.R;
import com.sophos.anviron.dao.FileScanMappingDAO;
import com.sophos.anviron.models.Detection;
import com.sophos.anviron.util.main.APIWrapper;
import com.sophos.anviron.util.main.CommonUtils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.List;

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
        try {
            Log.i("ScanService", "Running ScanService...");

            while (true) {

                DatabaseService dbInstance = DatabaseService.getInstance(getApplicationContext());

                //Handle quick scan (without submission) requests
                List<FileScanMappingDAO.CustomJoinFileScanMapping> customJoinFileScanMappings = dbInstance.getMappingDAO().getFilesByStatus("in progress");

                try {
                    for (FileScanMappingDAO.CustomJoinFileScanMapping mapping : customJoinFileScanMappings) {

                        String fileSHA256 = CommonUtils.getSHA256(mapping.filePath);

                        HashMap<String, String> params_map = new HashMap<>();
                        HashMap<String, String> fileOrJobId = new HashMap<>();

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

                        Long reputationScore = null;

                        JSONParser parser = new JSONParser();


                        if (mapping.scanType.equalsIgnoreCase("quick")) {

                            JSONObject responseJson = (JSONObject) parser.parse(report_map.get(fileSHA256));
                            reputationScore = (Long) responseJson.get("reputationScore");

                        }
                        else if(mapping.scanType.equalsIgnoreCase("static")) {
                            Log.i("report static", report_map.toString());

                        }

                        /*String detectionType = CommonUtils.getDetectionType(reputationScore);

                        if (detectionType.equalsIgnoreCase("malware") || detectionType.equalsIgnoreCase("pua")) {

                            String detectionId = dbInstance.getDetectionDAO().getDetectionsByFileId(mapping.fileId);

                            Detection detection = new Detection();

                            detection.setDetection_name(detectionType);
                            detection.setDetection_type(detectionType);
                            detection.setFile_id(mapping.fileId);
                            detection.setRep_score(reputationScore);
                            detection.setStatus("detected");
                            detection.setDetection_time(CommonUtils.getCurrentDateTime());

                            if (detectionId == null) {
                                detection.setDetection_id(CommonUtils.generateUUID());
                                dbInstance.getDetectionDAO().insert(detection);
                            } else {
                                detection.setDetection_id(detectionId);
                                dbInstance.getDetectionDAO().update(detection);
                            }
                        }

                        dbInstance.getMappingDAO().updateStatus("completed", mapping.scanId, mapping.fileId);*/

                    }

                    // Log.i("report detection: ", dbInstance.getDetectionDAO().getDetections().toString());
                    Log.i("report scans: ", dbInstance.getScanDAO().getScanInfo().toString());
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
