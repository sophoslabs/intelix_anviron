package com.sophos.anviron.service.main;

import android.util.Log;

import com.sophos.anviron.util.main.APIWrapper;
import com.sophos.anviron.util.main.CommonUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanService {

    ArrayList<File> filesToScan = new ArrayList<>();
    private static String api = "https://de.api.labs.sophos.com";
    private static String api_endpoint = "lookup/files/v1";
    private static String corelation_id = null;

    public ScanService(ArrayList<File> filesToScan) {
        this.filesToScan = filesToScan;
    }

    public String scanFiles() {
        try {
            for (File file : filesToScan) {
                String fileSHA256 = CommonUtils.getSHA256(file.toString());
                Log.i("sha256", fileSHA256);

                HashMap<String, String> params_map = new HashMap<String, String>();
                HashMap<String, String> file_orjob_id = new HashMap<String, String>();
                file_orjob_id.put("sha256", fileSHA256);
                APIWrapper api_wrapper = new APIWrapper(api, api_endpoint, corelation_id, params_map, file_orjob_id);
                HashMap<String, String> report_map = api_wrapper.get_file_report();
                Log.i("report",report_map.get(fileSHA256));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
