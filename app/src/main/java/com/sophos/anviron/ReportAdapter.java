package com.sophos.anviron;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.models.Scan;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

    private List<ScanDAO.ScanReport> scanReports;

    public ReportAdapter(List<ScanDAO.ScanReport> scanReports) {
        this.scanReports = scanReports;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ScanDAO.ScanReport scanReport = scanReports.get(position);
        holder.rowScanId.setText(scanReport.scan_id);
        if (scanReport.is_file_uploaded){
            holder.isFileUploaded.setText("Files sent for remote analysis");
        }
        else{
            holder.isFileUploaded.setText("No files sent for remote analysis");
        }
        holder.rowSubmissionTime.setText("Submission: "+scanReport.submission_time);
        holder.rowCompletionTime.setText("Completion: "+scanReport.completion_time);
//        holder.rowStatus.setText("Scan Type:" +scanReport.status);
        holder.rowTotalFiles.setText("Total Files Scanned: "+scanReport.total_files.toString());
        holder.rowScanType.setText("Scan Type: "+scanReport.scan_type);
    }

    @Override
    public int getItemCount() {
        return scanReports.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView rowScanId, rowSubmissionTime, rowCompletionTime, rowStatus, rowScanType, rowTotalFiles, isFileUploaded;

public MyViewHolder(View view) {
            super(view);
            rowScanId = view.findViewById(R.id.rowScanId);
            isFileUploaded = view.findViewById(R.id.isFileUploaded);
            rowSubmissionTime = view.findViewById(R.id.rowSubmissionTime);
            rowCompletionTime = view.findViewById(R.id.rowCompletionTime);
            rowStatus = view.findViewById(R.id.rowScanStatus);
            rowScanType = view.findViewById(R.id.rowScanType);
            rowTotalFiles = view.findViewById(R.id.rowTotalFiles);
        }
    }
}


