package com.sophos.anviron;

import android.support.annotation.NonNull;
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
        holder.isFileUploaded.setChecked(scanReport.is_file_uploaded);
        holder.isFileUploaded.setEnabled(false);
        holder.isFileUploaded.setText(new String("Files uploaded:"));
        holder.rowSubmissionTime.setText(scanReport.submission_time);
        holder.rowCompletionTime.setText(scanReport.completion_time);
        holder.rowStatus.setText(scanReport.status);
    }

    @Override
    public int getItemCount() {
        return scanReports.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView rowScanId, rowSubmissionTime, rowCompletionTime, rowStatus, rowScanType, rowTotalFiles;
        public CheckedTextView isFileUploaded;

        public MyViewHolder(View view) {
            super(view);
            rowScanId = view.findViewById(R.id.rowScanId);
            isFileUploaded = view.findViewById(R.id.isFileUploadedcheckBox);
            rowSubmissionTime = view.findViewById(R.id.rowSubmissionTime);
            rowCompletionTime = view.findViewById(R.id.rowCompletionTime);
            rowStatus = view.findViewById(R.id.rowScanStatus);
            rowScanType = view.findViewById(R.id.rowScanType);
            rowTotalFiles = view.findViewById(R.id.rowTotalFiles);
        }
    }
}


