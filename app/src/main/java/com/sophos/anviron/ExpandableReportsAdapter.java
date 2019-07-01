package com.sophos.anviron;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.service.main.DatabaseService;
import com.sophos.anviron.util.main.CommonUtils;

import org.w3c.dom.Text;

public class ExpandableReportsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, ScanDAO.ScanReport> expandableListDetail;

    public ExpandableReportsAdapter(Context context, List<String> expandableListTitle,
                                    HashMap<String, ScanDAO.ScanReport> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition));
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View view, ViewGroup parent) {
        ScanDAO.ScanReport scanReport = (ScanDAO.ScanReport) getChild(listPosition, expandedListPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.navigation_reports_list_row, null);
        }

        TextView rowScanId = view.findViewById(R.id.rowScanId);
        TextView rowIsFileUploaded = view.findViewById(R.id.rowIsFileUploaded);
        TextView rowTotalDetections = view.findViewById(R.id.rowTotalDetections);
        TextView rowCompletionTime = view.findViewById(R.id.rowCompletionTime);
        TextView rowTotalFiles = view.findViewById(R.id.rowTotalFiles);
        TextView rowTotalCost = view.findViewById(R.id.rowTotalCost);

        rowScanId.setText(scanReport.scan_id);
        if (scanReport.is_file_uploaded) {
            rowIsFileUploaded.setText("Files sent for remote analysis");
        } else {
            rowIsFileUploaded.setText("No files sent for remote analysis");
        }

        if (scanReport.completion_time == null) {
            rowCompletionTime.setVisibility(view.GONE);
        } else {
            rowCompletionTime.setText("Completion: " + scanReport.completion_time);
            rowCompletionTime.setVisibility(view.VISIBLE);
        }
        rowTotalFiles.setText("Files submitted: " + scanReport.total_files.toString());

        DatabaseService dbInstance = DatabaseService.getInstance(view.getContext().getApplicationContext());
        rowTotalDetections.setText("Files detected: " + dbInstance.getDetectionDAO().getDetectionsByScanId(scanReport.scan_id).toString());

        // Logic to calculate cost

        Double quickScanCost = Double.parseDouble(view.getContext().getString(R.string.quick_cost_dollar));
        Double staticScanCost = Double.parseDouble(view.getContext().getString(R.string.static_cost_dollar));
        Double dynamicScanCost = Double.parseDouble(view.getContext().getString(R.string.dynamic_cost_dollar));

        Double totalScanCost = 0.0;

        switch(scanReport.scan_type){
            case "quick":
                totalScanCost  = quickScanCost * Double.parseDouble(scanReport.total_files);
                break;
            case "static":
                totalScanCost  = staticScanCost * Double.parseDouble(scanReport.total_files);
                break;
            case "dynamic":
                totalScanCost  = dynamicScanCost * Double.parseDouble(scanReport.total_files);
                break;
        }

        DecimalFormat df3 = new DecimalFormat(view.getContext().getString(R.string.decimal_format_3_precision));

        rowTotalCost.setText(df3.format(totalScanCost) + " $");

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View view, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.navigation_reports_list_group, null);
        }
        TextView listTitleTextView = view.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);

        ProgressBar progressBar = view.findViewById(R.id.progressBarScanStatus);
        TextView listScanSubmittedTime = view.findViewById(R.id.listScanSubmittedTime);
        listTitleTextView.setTypeface(null, Typeface.BOLD);

        ScanDAO.ScanReport scanReport = expandableListDetail.get(listTitle);

        String title = scanReport.scan_type + " Scan";
        String capitalizeTitleText = title.substring(0,1).toUpperCase() + title.substring(1);
        listTitleTextView.setText(capitalizeTitleText);

        if(scanReport.scan_status.equalsIgnoreCase("completed")) {
            progressBar.setVisibility(view.INVISIBLE);
        }
        else
        {
            progressBar.setVisibility(view.VISIBLE);
        }

        listScanSubmittedTime.setText(scanReport.submission_time);

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
