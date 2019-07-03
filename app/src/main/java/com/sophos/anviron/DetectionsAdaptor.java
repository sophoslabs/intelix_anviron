package com.sophos.anviron;

import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.service.main.DatabaseService;
import com.sophos.anviron.util.main.CommonUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DetectionsAdaptor extends RecyclerView.Adapter<DetectionsAdaptor.MyViewHolder> {

    private List<DetectionDAO.FileDetectionMapping> fileDetectionMappings;

    public DetectionsAdaptor(List<DetectionDAO.FileDetectionMapping> fileDetectionMappings) {
        this.fileDetectionMappings = fileDetectionMappings;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_detection_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final DetectionDAO.FileDetectionMapping fileDetectionMapping = fileDetectionMappings.get(position);

        String fileName = CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath);
        String shortenedFilename = fileName;

        if (fileName.length() >= 25) {
            shortenedFilename = fileName.substring(0, 20);
            shortenedFilename += "...";
            shortenedFilename += fileName.substring(fileName.length() - 4);
        }

        holder.rowFileName.setText(shortenedFilename);

        if (fileDetectionMapping.detectionStatus.equalsIgnoreCase("clean")) {

            holder.btnQuarantineFile.setText("QUARANTINE");
            holder.btnMarkClean.setText("MARK AS UNCLEAN");
            holder.rowDetectionState.setText("MARKED CLEAN");

            holder.btnDeleteFile.setEnabled(false);
            holder.btnMarkClean.setEnabled(true);
            holder.btnQuarantineFile.setEnabled(false);
            holder.btnRecoverFile.setEnabled(false);

            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));

        } else if (fileDetectionMapping.detectionStatus.equalsIgnoreCase("deleted")) {
            holder.rowDetectionState.setText(fileDetectionMapping.detectionName);
            holder.btnDeleteFile.setText("DELETED");
            holder.btnMarkClean.setText("MARK CLEAN");
            holder.btnQuarantineFile.setText("QUARANTINE");

            holder.btnDeleteFile.setEnabled(false);
            holder.btnMarkClean.setEnabled(false);
            holder.btnQuarantineFile.setEnabled(false);
            holder.btnRecoverFile.setEnabled(false);

            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));

        } else if (fileDetectionMapping.detectionStatus.equalsIgnoreCase("detected")) {
            holder.rowDetectionState.setText(fileDetectionMapping.detectionName);

            holder.btnDeleteFile.setText("DELETE");
            holder.btnMarkClean.setText("MARK CLEAN");
            holder.btnQuarantineFile.setText("QUARANTINE");

            holder.btnDeleteFile.setEnabled(true);
            holder.btnMarkClean.setEnabled(true);
            holder.btnQuarantineFile.setEnabled(true);
            holder.btnRecoverFile.setEnabled(false);

            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.detection_card));

        } else if (fileDetectionMapping.detectionStatus.equalsIgnoreCase("quarantined")) {

            holder.rowDetectionState.setText(fileDetectionMapping.detectionName);
            holder.btnDeleteFile.setText("DELETE");
            holder.btnQuarantineFile.setText("QUARANTINED");
            holder.btnMarkClean.setText("MARK CLEAN");

            holder.btnDeleteFile.setEnabled(false);
            holder.btnQuarantineFile.setEnabled(false);
            holder.btnMarkClean.setEnabled(false);
            holder.btnRecoverFile.setEnabled(true);

            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
        }


        holder.rowAnalysisDate.setText("Detection Time: " + fileDetectionMapping.detectionTime);

        holder.rowFolderPath.setText("File Location : " + CommonUtils.getFolderPathFromFilePath(fileDetectionMapping.filePath));

        holder.btnDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    File file = new File(fileDetectionMapping.filePath);

                    if (file.exists()) {
                        file.delete();
                    }

                    DatabaseService dbInstance = DatabaseService.getInstance(v.getContext());
                    dbInstance.getDetectionDAO().updateStatusByDetectionId("deleted", fileDetectionMapping.detectionId);

                    holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
                    holder.btnDeleteFile.setText("DELETED");
                    holder.btnMarkClean.setText("MARK CLEAN");
                    holder.btnDeleteFile.setEnabled(false);
                    holder.btnMarkClean.setEnabled(false);
                    holder.btnQuarantineFile.setEnabled(false);

                } catch (Exception e) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Not able to delete " + fileDetectionMapping.filePath + ". Please delete manually.", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.btnMarkClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseService dbInstance = DatabaseService.getInstance(v.getContext());


                if (holder.btnMarkClean.getText().toString().equalsIgnoreCase("MARK CLEAN")) {

                    dbInstance.getDetectionDAO().updateStatusByDetectionId("clean", fileDetectionMapping.detectionId);
                    holder.btnQuarantineFile.setEnabled(false);
                    holder.btnDeleteFile.setText("DELETE");
                    holder.btnDeleteFile.setEnabled(false);
                    holder.btnMarkClean.setText("MARK AS UNCLEAN");
                    holder.btnMarkClean.setEnabled(true);
                    holder.rowDetectionState.setText("MARKED CLEAN");
                    holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
                } else if (holder.btnMarkClean.getText().toString().equalsIgnoreCase("MARK AS UNCLEAN")) {

                    dbInstance.getDetectionDAO().updateStatusByDetectionId("detected", fileDetectionMapping.detectionId);
                    holder.rowDetectionState.setText(fileDetectionMapping.detectionName);
                    holder.btnMarkClean.setText("MARK CLEAN");
                    holder.btnDeleteFile.setText("DELETE");
                    holder.btnMarkClean.setEnabled(true);
                    holder.btnDeleteFile.setEnabled(true);
                    holder.btnQuarantineFile.setEnabled(true);
                    holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.detection_card));
                }
            }
        });


        holder.btnQuarantineFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    File srcFile = new File(fileDetectionMapping.filePath);
                    File destDir = new File(new ContextWrapper(v.getContext().getApplicationContext()).getFilesDir().toString() + "/quarantine");
                    if (!destDir.exists()) {
                        destDir.mkdir();
                    }
                    //String destFileName = destDir.toString() + '/' + Uri.parse(fileDetectionMapping.filePath).getLastPathSegment().replace('.', '_') + ".zip";

                    String destFileName = destDir.toString() + '/' + fileDetectionMapping.fileId + ".data";


                    if (srcFile.exists()) {

                        Log.i("Compressing file: ", fileDetectionMapping.filePath);
                        FileOutputStream dest = new FileOutputStream(destFileName);
                        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
                        FileInputStream fileInputStream = new FileInputStream(srcFile);
                        BufferedInputStream origin = new BufferedInputStream(fileInputStream);
                        ZipEntry entry = new ZipEntry(CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath));
                        out.putNextEntry(entry);

                        byte data[] = new byte[1000];
                        int count;
                        while ((count = origin.read(data, 0, 1000)) != -1) {
                            out.write(data, 0, count);
                        }
                        origin.close();
                        out.close();

                        // Update database with quarantine state
                        DatabaseService dbInstance = DatabaseService.getInstance(v.getContext());
                        dbInstance.getDetectionDAO().updateStatusByDetectionId("quarantined", fileDetectionMapping.detectionId);

                        srcFile.delete();

                        holder.btnQuarantineFile.setText("QUARANTINED");
                        holder.btnDeleteFile.setEnabled(false);
                        holder.btnMarkClean.setEnabled(false);
                        holder.btnQuarantineFile.setEnabled(false);

                        // Updating the UI
                        holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
                        Toast.makeText(v.getContext().getApplicationContext(), "File " + CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath) + " has been quarantined.", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(v.getContext().getApplicationContext(), "Not able to quarantine file " + CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath) + " as file do not exists", Toast.LENGTH_LONG).show();
                        DatabaseService dbInstance = DatabaseService.getInstance(v.getContext());
                        dbInstance.getDetectionDAO().updateStatusByDetectionId("deleted", fileDetectionMapping.detectionId);

                        holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));

                        holder.btnDeleteFile.setText("DELETED");
                        holder.btnMarkClean.setText("MARK CLEAN");
                        holder.btnQuarantineFile.setText("QUARANTINE");

                        holder.btnDeleteFile.setEnabled(false);
                        holder.btnMarkClean.setEnabled(false);
                        holder.btnQuarantineFile.setEnabled(false);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext().getApplicationContext(), "Not able to quarantine file " + CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.btnRecoverFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    String srcZipFilePath = new ContextWrapper(v.getContext().getApplicationContext()).getFilesDir().toString() + "/quarantine/" + fileDetectionMapping.fileId + ".data";
                    String destDirPath = CommonUtils.getFolderPathFromFilePath(fileDetectionMapping.filePath);

                    File destDir = new File(destDirPath);
                    File srcZipFile = new File(srcZipFilePath);

                    if (!destDir.exists()) {
                        destDir.mkdir();
                    }

                    if (srcZipFile.exists()) {

                        Log.i("Decompressing file: ", srcZipFilePath);
                        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(srcZipFilePath));
                        byte[] buffer = new byte[1024];
                        ZipEntry zipEntry = zipInputStream.getNextEntry();

                        while (zipEntry != null) {
                            FileOutputStream fileOutputStream = new FileOutputStream(fileDetectionMapping.filePath);
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                fileOutputStream.write(buffer, 0, len);
                            }
                            fileOutputStream.close();
                            zipEntry = zipInputStream.getNextEntry();
                        }

                        zipInputStream.closeEntry();
                        zipInputStream.close();

                        // Update database with detected state
                        DatabaseService dbInstance = DatabaseService.getInstance(v.getContext());
                        dbInstance.getDetectionDAO().updateStatusByDetectionId("detected", fileDetectionMapping.detectionId);

                        srcZipFile.delete();

                        holder.btnQuarantineFile.setText("QUARANTINE");

                        holder.btnRecoverFile.setEnabled(false);
                        holder.btnDeleteFile.setEnabled(true);
                        holder.btnMarkClean.setEnabled(true);
                        holder.btnQuarantineFile.setEnabled(true);

                        // Updating the UI
                        holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.detection_card));
                        Toast.makeText(v.getContext().getApplicationContext(), "File " + CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath) + " has been recovered at "+CommonUtils.getFolderPathFromFilePath(fileDetectionMapping.filePath), Toast.LENGTH_LONG).show();

                    } else {

                        Toast.makeText(v.getContext().getApplicationContext(), "Not able to recover file " + CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath), Toast.LENGTH_LONG).show();
                        holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));

                        holder.btnDeleteFile.setText("DELETE");
                        holder.btnMarkClean.setText("MARK CLEAN");
                        holder.btnQuarantineFile.setText("QUARANTINED");

                        holder.btnDeleteFile.setEnabled(false);
                        holder.btnMarkClean.setEnabled(false);
                        holder.btnQuarantineFile.setEnabled(false);
                        holder.btnRecoverFile.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext().getApplicationContext(), "Not able to recover file " + CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return fileDetectionMappings.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView rowFileName, rowFolderPath, rowDetectionState, rowAnalysisDate;
        public Button btnDeleteFile, btnMarkClean, btnQuarantineFile, btnRecoverFile;

        public MyViewHolder(View view) {
            super(view);
            rowFileName = view.findViewById(R.id.rowFileName);
            rowDetectionState = view.findViewById(R.id.rowDetectionState);
            rowAnalysisDate = view.findViewById(R.id.rowAnalysisDate);
            rowFolderPath = view.findViewById(R.id.rowFolderPath);
            btnDeleteFile = view.findViewById(R.id.btnDeleteFile);
            btnMarkClean = view.findViewById(R.id.btnMarkClean);
            btnQuarantineFile = view.findViewById(R.id.btnQuarantineFile);
            btnRecoverFile = view.findViewById(R.id.btnRecoverFile);
        }
    }

}
