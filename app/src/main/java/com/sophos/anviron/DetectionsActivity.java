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
        setNavigationViewListener();
    }


    private void prepareFileDetectionData(DetectionsActivity activity) {

        // Somehow we need to add this dummy header which is hide behind the view area perhaps behind the header (or are values for header place holders)
        fileDetectionMappingsList.add(new DetectionDAO.FileDetectionMapping("fileId", "filePath","detectionId", "detectionType", "detectionName", "detectionTime", "detectionStatus"));

        DetectionViewModel detectionViewModel = ViewModelProviders.of( (DetectionsActivity)activity).get(DetectionViewModel.class);

        detectionViewModel.getLiveFileDetectionMappings().observe(this, new android.arch.lifecycle.Observer<List<DetectionDAO.FileDetectionMapping>>() {
            @Override
            public void onChanged(@Nullable List<DetectionDAO.FileDetectionMapping> fileDetectionMappings) {

                for (DetectionDAO.FileDetectionMapping fileDetectionMapping:fileDetectionMappings){

                    boolean fileDetectionMappingDuplicate = false;
                    for (DetectionDAO.FileDetectionMapping fileDetectionMapping1:fileDetectionMappingsList) {
                        if (fileDetectionMapping.detectionId.equalsIgnoreCase(fileDetectionMapping1.detectionId))
                            fileDetectionMappingDuplicate = true;
                    }

                    if(fileDetectionMappingDuplicate==false)
                        fileDetectionMappingsList.add(fileDetectionMapping);
                }
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
        bottomNavigationView.setItemBackgroundResource(R.drawable.navi_background);
        bottomNavigationView.setPressed(true);
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