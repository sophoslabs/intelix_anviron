package com.sophos.anviron;

import android.graphics.drawable.Drawable;
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

import java.io.File;
import java.util.List;

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
            shortenedFilename += fileName.substring(fileName.length() - 4, fileName.length() - 1);
        }

        holder.rowFileName.setText(shortenedFilename);

        if (fileDetectionMapping.detectionStatus.equalsIgnoreCase("clean")) {

            holder.btnMarkClean.setText("MARK AS UNCLEAN");
            holder.rowDetectionState.setText("MARKED CLEAN");
            holder.btnDeleteFile.setEnabled(false);
            holder.btnMarkClean.setEnabled(true);
            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
        } else if (fileDetectionMapping.detectionStatus.equalsIgnoreCase("deleted")) {
            holder.rowDetectionState.setText(fileDetectionMapping.detectionName);
            holder.btnDeleteFile.setText("DELETED");
            holder.btnMarkClean.setText("MARK CLEAN");
            holder.btnDeleteFile.setEnabled(false);
            holder.btnMarkClean.setEnabled(false);
            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
        } else if (fileDetectionMapping.detectionStatus.equalsIgnoreCase("detected")) {
            holder.rowDetectionState.setText(fileDetectionMapping.detectionName);
            holder.btnDeleteFile.setText("DELETE");
            holder.btnMarkClean.setText("MARK CLEAN");
            holder.btnDeleteFile.setEnabled(true);
            holder.btnMarkClean.setEnabled(true);
            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.detection_card));
        }

        holder.rowAnalysisDate.setText("Detection Time: " + fileDetectionMapping.detectionTime);

        holder.rowFolderPath.setText("File Location : " + CommonUtils.getFolderPathFromFilePath(fileDetectionMapping.filePath));

        holder.btnDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseService dbInstance = DatabaseService.getInstance(v.getContext());
                holder.btnDeleteFile.setText("DELETED");
                holder.btnMarkClean.setText("MARK CLEAN");
                holder.btnDeleteFile.setEnabled(false);
                holder.btnMarkClean.setEnabled(false);

                try {

                    File file = new File(fileDetectionMapping.filePath);
                    file.delete();
                    dbInstance.getDetectionDAO().updateStatusByDetectionId("deleted", fileDetectionMapping.detectionId);
                    holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));

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
                    holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.detection_card));
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
        public Button btnDeleteFile, btnMarkClean;

        public MyViewHolder(View view) {
            super(view);
            rowFileName = view.findViewById(R.id.rowFileName);
            rowDetectionState = view.findViewById(R.id.rowDetectionState);
            rowAnalysisDate = view.findViewById(R.id.rowAnalysisDate);
            rowFolderPath = view.findViewById(R.id.rowFolderPath);
            btnDeleteFile = view.findViewById(R.id.btnDeleteFile);
            btnMarkClean = view.findViewById(R.id.btnMarkClean);
        }
    }

}
