package com.sophos.anviron;

import android.util.Log;
import java.io.File;
import java.util.ArrayList;

public class Utils {

    public static ArrayList<File> getAllNestedFilesRecursively(ArrayList<File> allFiles, File dir) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    allFiles = getAllNestedFilesRecursively(allFiles, file);
                } else {
                    allFiles.add(file);
                }
            }
        } catch (Exception e) {
            Log.i("Exception", e.toString());
        }
        return allFiles;
    }
}
