package com.sophos.anviron;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sophos.anviron.dao.ScanDAO;

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

        rowScanId.setText(scanReport.scan_id);
        if (scanReport.is_file_uploaded) {
            rowIsFileUploaded.setText("Files sent for remote analysis");
        } else {
            rowIsFileUploaded.setText("No files sent for remote analysis");
        }

        if (scanReport.completion_time == null) {
            rowCompletionTime.setText("Completion: - ");
        } else {
            rowCompletionTime.setText("Completion: " + scanReport.completion_time);
        }
        rowTotalFiles.setText("Total Files Scanned: " + scanReport.total_files.toString());

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

        TextView listScanStatusTextView = view.findViewById(R.id.listScanStatus);
        TextView listScanSubmittedTime = view.findViewById(R.id.listScanSubmittedTime);
        listTitleTextView.setTypeface(null, Typeface.BOLD);

        ScanDAO.ScanReport scanReport = expandableListDetail.get(listTitle);

        String title = scanReport.scan_type + " Scan";
        String capitalizeTitleText = title.substring(0,1).toUpperCase() + title.substring(1);
        listTitleTextView.setText(capitalizeTitleText);

        listScanStatusTextView.setText(scanReport.scan_status);
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
