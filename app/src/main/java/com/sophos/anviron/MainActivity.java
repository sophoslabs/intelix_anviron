package com.sophos.anviron;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import com.sophos.anviron.models.Scan;
import com.sophos.anviron.service.main.DatabaseService;
import com.sophos.anviron.service.main.ScanService;
import com.sophos.anviron.ui.main.SectionsPagerAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static String api_client;
    public static String api_secret;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseService db_instance = DatabaseService.getInstance(this.getApplicationContext());

        String scan_info = db_instance.getScanDAO().getScanInfo().toString();

        Log.i("scan_info", scan_info);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        //View v = LayoutInflater.from(this).inflate(R.layout.quick_scan_fragment, null);
        // viewPager.addView(v);
        tabs.setupWithViewPager(viewPager);
        setNavigationViewListener();

        api_client = getString(R.string.client);
        api_secret = getString(R.string.secret);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = new Intent(this, ScanService.class);
        startService(intent);

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