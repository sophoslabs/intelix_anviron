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

        if(fileDetectionMapping.detectionStatus.equalsIgnoreCase("clean")){
            holder.btnMarkClean.setText("MARKED CLEAN");
            holder.btnDeleteFile.setEnabled(false);
            holder.btnMarkClean.setEnabled(false);
            holder.rowDetectionState.setText("CLEAN");
            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
        }

        else if(fileDetectionMapping.detectionStatus.equalsIgnoreCase("deleted")){
            holder.btnDeleteFile.setText("DELETED");
            holder.btnDeleteFile.setEnabled(false);
            holder.btnMarkClean.setEnabled(false);
            holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
        }


        holder.rowAnalysisDate.setText("Detection Time: "+fileDetectionMapping.detectionTime);

        holder.rowFolderPath.setText("File Location : "+CommonUtils.getFolderPathFromFilePath(fileDetectionMapping.filePath));

        holder.btnDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseService dbInstance = DatabaseService.getInstance(v.getContext());
                holder.btnDeleteFile.setText("DELETED");
                holder.btnDeleteFile.setEnabled(false);
                holder.btnMarkClean.setEnabled(false);

                try {

                    File file = new File(fileDetectionMapping.filePath);
                    file.delete();
                    dbInstance.getDetectionDAO().updateStatusByDetectionId("deleted",fileDetectionMapping.detectionId);
                    holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));

                }catch (Exception e){
                    Toast.makeText(v.getContext().getApplicationContext(), "Not able to delete " + fileDetectionMapping.filePath+". Please delete manually.", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.btnMarkClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseService dbInstance = DatabaseService.getInstance(v.getContext());
                dbInstance.getDetectionDAO().updateStatusByDetectionId("clean",fileDetectionMapping.detectionId);

                holder.btnMarkClean.setText("MARKED CLEAN");
                holder.btnDeleteFile.setEnabled(false);
                holder.btnMarkClean.setEnabled(false);
                holder.rowDetectionState.setText("CLEAN");

                holder.itemView.findViewById(R.id.rowDetectionItem).setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.report_card));
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
            rowFileName =  view.findViewById(R.id.rowFileName);
            rowDetectionState =  view.findViewById(R.id.rowDetectionState);
            rowAnalysisDate = view.findViewById(R.id.rowAnalysisDate);
            rowFolderPath = view.findViewById(R.id.rowFolderPath);
            btnDeleteFile = view.findViewById(R.id.btnDeleteFile);
            btnMarkClean = view.findViewById(R.id.btnMarkClean);
        }
    }

}
