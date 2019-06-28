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

                        JSONParser parser = new JSONParser();
                        JSONObject responseJson = (JSONObject) parser.parse(report_map.get(fileSHA256));
                        Long reputationScore = (Long)responseJson.get("reputationScore");

                        String detectionType = CommonUtils.getDetectionType(reputationScore);

                        if (detectionType.equalsIgnoreCase("MALWARE") || detectionType.equalsIgnoreCase("PUA")) {
                            Detection detection = new Detection();
                            detection.setDetection_id(CommonUtils.generateUUID());
                            detection.setDetection_name(detectionType);
                            detection.setDetection_type(detectionType);
                            detection.setFile_id(mapping.fileId);
                            detection.setRep_score(reputationScore);
                            detection.setDetection_time(CommonUtils.getCurrentDateTime());
                            dbInstance.getDetectionDAO().insert(detection);
                        }

                        dbInstance.getMappingDAO().updateStatus("completed", mapping.scanId, mapping.fileId);

                    }

                    Log.i("report detection: ", dbInstance.getDetectionDAO().getDetections().toString());
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
