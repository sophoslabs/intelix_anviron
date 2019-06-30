package com.sophos.anviron;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.util.main.CommonUtils;

import java.util.List;

public class DetectionsAdaptor extends RecyclerView.Adapter<DetectionsAdaptor.MyViewHolder>{

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

        holder.rowFileName.setText(CommonUtils.getFileNameFromFilePath(fileDetectionMapping.filePath));

        holder.rowDetectionState.setText(fileDetectionMapping.detectionName);

        holder.rowAnalysisDate.setText("Detection Time: "+fileDetectionMapping.detectionTime);

        holder.rowFolderPath.setText("File Location : "+CommonUtils.getFolderPathFromFilePath(fileDetectionMapping.filePath));
    }

    @Override
    public int getItemCount() {
        return fileDetectionMappings.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView rowFileName, rowFolderPath, rowDetectionState, rowAnalysisDate;

        public MyViewHolder(View view) {
            super(view);
            rowFileName =  view.findViewById(R.id.rowFileName);
            rowDetectionState =  view.findViewById(R.id.rowDetectionState);
            rowAnalysisDate = view.findViewById(R.id.rowAnalysisDate);
            rowFolderPath = view.findViewById(R.id.rowFolderPath);
        }
    }

}
