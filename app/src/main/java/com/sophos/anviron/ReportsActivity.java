package com.sophos.anviron;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.models.ReportViewModel;
import com.sophos.anviron.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private List<ScanDAO.ScanReport> scanReportList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_reports);
        recyclerView = findViewById(R.id.report_recycle_view);
        reportAdapter = new ReportAdapter(scanReportList);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reportAdapter);
        prepareFileDetectionData(this);
        setNavigationViewListener();
    }

    private void prepareFileDetectionData(ReportsActivity activity) {

        // Somehow we need to add this dummy header which is hide behind the view area perhaps behind the header (or are values for header place holders)
        scanReportList.add(
                new ScanDAO.ScanReport("scan_id",
                                        "type",
                        false,
                        "submission_time",
                        "completion_time", 0,
                        "status"));

        ReportViewModel reportViewModel = ViewModelProviders.of( (ReportsActivity)activity).get(ReportViewModel.class);

        reportViewModel.getScanReport().observe(this, new android.arch.lifecycle.Observer<List<ScanDAO.ScanReport>>() {
            @Override
            public void onChanged(@Nullable List<ScanDAO.ScanReport> scanReports) {
                scanReportList.clear();
                scanReportList.addAll(scanReports);
                reportAdapter.notifyDataSetChanged();
            }
        });
        reportAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    private void setNavigationViewListener() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_scans: {
                Intent intent = new Intent(ReportsActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.navigation_reports: {
                break;
            }
            case R.id.navigation_detections: {
                Intent intent = new Intent(ReportsActivity.this, DetectionsActivity.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

}