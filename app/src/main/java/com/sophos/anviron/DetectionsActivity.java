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
import android.view.Menu;
import android.view.MenuItem;

import com.sophos.anviron.dao.DetectionDAO;
import com.sophos.anviron.models.Detection;
import com.sophos.anviron.models.DetectionViewModel;
import com.sophos.anviron.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetectionsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private List<DetectionDAO.FileDetectionMapping> fileDetectionMappingsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private  DetectionsAdaptor detectionsAdaptor;

    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_detections);

        recyclerView = findViewById(R.id.recycler_view);
        detectionsAdaptor = new DetectionsAdaptor(fileDetectionMappingsList);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(detectionsAdaptor);
        prepareFileDetectionData(this);

        //Stuffs related to bottom navigation view
        setNavigationViewListener();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.navigation_detections).setChecked(true);
    }


    private void prepareFileDetectionData(DetectionsActivity activity) {

        DetectionViewModel detectionViewModel = ViewModelProviders.of( (DetectionsActivity)activity).get(DetectionViewModel.class);

        detectionViewModel.getLiveFileDetectionMappings().observe(this, new android.arch.lifecycle.Observer<List<DetectionDAO.FileDetectionMapping>>() {
            @Override
            public void onChanged(@Nullable List<DetectionDAO.FileDetectionMapping> fileDetectionMappings) {

                fileDetectionMappingsList.clear();

                fileDetectionMappingsList.addAll(fileDetectionMappings);

                detectionsAdaptor.notifyDataSetChanged();
            }
        });
        detectionsAdaptor.notifyDataSetChanged();
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
                Intent intent = new Intent(DetectionsActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.navigation_reports: {
                Intent intent = new Intent(DetectionsActivity.this, ReportsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.navigation_detections: {
                break;
            }
        }
        return true;
    }

}