package com.sophos.anviron.util.main;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;


public class CommonUtils {

    public static ArrayList<File> getAllNestedFilesRecursively(ArrayList<File> allFiles, File dir) {

        File[] files;

        try {
            files = dir.listFiles();
            if (files!=null && files.length>0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        allFiles = getAllNestedFilesRecursively(allFiles, file);
                    } else {
                        allFiles.add(file);
                    }
                }
            }

        }catch (Exception e){
            return allFiles;
        }
        return allFiles;
    }

    public static ArrayList<File> getAllFilesInUserSpace(){

        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(rootPath);
        ArrayList<File> files = new ArrayList<File>();
        files = getAllNestedFilesRecursively(files, file);
        return files;
    }

    public static Integer listItemsDiffsCount(ArrayList<String> allItems, ArrayList<String> checkItems){
        Integer diffCount = allItems.size();
        for (String itemSource: allItems) {
            for (String itemTarget : checkItems) {
                if (itemSource.equalsIgnoreCase(itemTarget)){
                    diffCount--;
                }
            }
        }
        return diffCount;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int value = b & 0xFF;
            if (value < 16) {
                // if value less than 16, then it's hex String will be only
                // one character, so we need to append a character of '0'
                sb.append("0");
            }
            sb.append(Integer.toHexString(value).toUpperCase());
        }
        return sb.toString();
    }

    public static String getSHA256(String filePath) throws NoSuchAlgorithmException, IOException
    {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
        byte[] bytes = new byte[1024];
        // read all file content
        while (digestInputStream.read(bytes) > 0);
        byte[] resultByteArray = digest.digest();
        return bytesToHexString(resultByteArray);
    }

    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }

    public static String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String getFileNameFromFilePath(String filePath){
        String[] fileSubPaths = filePath.split("/");
        return fileSubPaths[fileSubPaths.length-1];
    }

    public static String getFolderPathFromFilePath(String filePath){
        Integer index = filePath.lastIndexOf("/");
        return filePath.substring(0,index);
    }

    public static String getDetectionType(Long reputationScore){
        if (reputationScore>=0 && reputationScore<=19)
            return "malware";
        else if(reputationScore>=20 && reputationScore<=29)
            return "pua";
        else if (reputationScore>=30 && reputationScore<=69)
            return "unknown";
        else if (reputationScore>=70 && reputationScore<=100)
            return "clean";
        else return "invalid";
    }
}
