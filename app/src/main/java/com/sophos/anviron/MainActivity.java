package com.sophos.anviron;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.sophos.anviron.ui.main.SectionsPagerAdapter;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        //View v = LayoutInflater.from(this).inflate(R.layout.quick_scan_fragment, null);
        // viewPager.addView(v);
        tabs.setupWithViewPager(viewPager);
        setNavigationViewListener();

    }

    private void setNavigationViewListener() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_scans: {
                break;
            }
            case R.id.navigation_reports: {
                Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.navigation_detections: {
                Intent intent = new Intent(MainActivity.this, DetectionsActivity.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

}