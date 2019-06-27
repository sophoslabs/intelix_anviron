package com.sophos.anviron.util.main;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class CommonUtils {

    public static ArrayList<File> getAllNestedFilesRecursively(ArrayList<File> allFiles, File dir) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    allFiles = getAllNestedFilesRecursively(allFiles, file);
                } else {
                    allFiles.add(file);
                }
            }
        return allFiles;
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
}
