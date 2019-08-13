package com.sophos.anviron;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.sophos.anviron.dao.ScanDAO;
import com.sophos.anviron.models.ReportViewModel;
import com.sophos.anviron.service.main.DatabaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaymentActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private List<ScanDAO.ScanReport> scanReportList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReportsAdapter reportsAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    ExpandableListView expandableListView;
    ExpandableReportsAdapter expandableReportsAdapter;
    List<String> expandableListTitle = new ArrayList<>();
    HashMap<String, ScanDAO.ScanReport> expandableListDetail = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_reports);
        recyclerView = findViewById(R.id.report_recycle_view);
        reportsAdapter = new ReportsAdapter(scanReportList);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reportsAdapter);


        //Stuff related to bottom navigation view
        setNavigationViewListener();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.navigation_reports).setChecked(true);

        //Stuff related to expandableView
        expandableListView = findViewById(R.id.expandableListView);
        expandableReportsAdapter = new ExpandableReportsAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableReportsAdapter);
        prepareReportsData(this);
    }

    private void prepareReportsData(final PaymentActivity activity) {

        ReportViewModel reportViewModel = ViewModelProviders.of(activity).get(ReportViewModel.class);
        reportViewModel.getScanReport().observe(this, new android.arch.lifecycle.Observer<List<ScanDAO.ScanReport>>() {

            @Override
            public void onChanged(@Nullable List<ScanDAO.ScanReport> scanReports) {

                scanReportList.clear();
                expandableListTitle.clear();

                for (ScanDAO.ScanReport scanReport:scanReports) {
                    DatabaseService dbInstance = DatabaseService.getInstance(activity.getApplicationContext());
                    scanReport.scan_status = dbInstance.getMappingDAO().getScanStatusByScanId(scanReport.scan_id);
                    scanReportList.add(scanReport);
                    expandableListTitle.add(scanReport.scan_id);
                    expandableListDetail.put(scanReport.scan_id, scanReport);
                }

                reportsAdapter.notifyDataSetChanged();
                expandableReportsAdapter.notifyDataSetChanged();
            }
        });

        reportsAdapter.notifyDataSetChanged();
        expandableReportsAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    private void setNavigationViewListener() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemBackgroundResource(R.drawable.navi_background);
        bottomNavigationView.setPressed(true);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_scans: {
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.navigation_reports: {
                break;
            }
            case R.id.navigation_detections: {
                Intent intent = new Intent(PaymentActivity.this, DetectionsActivity.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

}