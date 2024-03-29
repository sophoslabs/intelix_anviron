/*
 * #  Copyright (c) 2019. Sophos Limited
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 */

package com.sophos.anviron;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sophos.anviron.dao.ScanDAO;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {

    private List<ScanDAO.ScanReport> scanReports;

    public ReportsAdapter(List<ScanDAO.ScanReport> scanReports) {
        this.scanReports = scanReports;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_reports_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ScanDAO.ScanReport scanReport = scanReports.get(position);
        holder.rowScanId.setText(scanReport.scan_id);
        if (scanReport.is_file_uploaded) {
            holder.isFileUploaded.setText("Files sent for remote analysis");
        } else {
            holder.isFileUploaded.setText("No files sent for remote analysis");
        }
        if (scanReport.completion_time == null) {
            holder.rowCompletionTime.setText("Completion: - ");
        } else {
            holder.rowCompletionTime.setText("Completion: " + scanReport.completion_time);
        }
        holder.rowTotalFiles.setText("Total Files Scanned: " + scanReport.total_files.toString());
    }

    @Override
    public int getItemCount() {
        return scanReports.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView rowScanId, rowCompletionTime, rowTotalFiles, isFileUploaded;

        public MyViewHolder(View view) {
            super(view);
            rowScanId = view.findViewById(R.id.rowScanId);
            rowCompletionTime = view.findViewById(R.id.rowCompletionTime);
            rowTotalFiles = view.findViewById(R.id.rowTotalFiles);
            isFileUploaded = view.findViewById(R.id.rowIsFileUploaded);
        }
    }
}


