package com.sophos.anviron.service.main;

import android.util.Log;

import com.sophos.anviron.util.main.CommonUtils;
import java.io.File;
import java.util.ArrayList;

public class ScanService {

    ArrayList<File> filesToScan = new ArrayList<>();

    public ScanService(ArrayList<File> filesToScan) {
        this.filesToScan = filesToScan;
    }

    public String scanFiles() {
        try {
            for (File file : filesToScan) {
                Log.i("sha256", CommonUtils.getSHA256(file.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
