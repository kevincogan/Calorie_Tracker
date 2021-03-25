package com.example.phaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.phaze.classes.User;
import com.example.phaze.fragments.CameraFragment;
import com.example.phaze.fragments.SettingsFragment;
import com.example.phaze.fragments.UserFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    FragmentPagerAdapter adapterViewPager;

    SensorManager sensorManager;
    boolean isRunning = false;
    public static float counter;

    BottomAppBar appBar;

    BottomNavigationView bottomNavigationView;
    FloatingActionButton home;

    String unselectedColor = "#b5b5b5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate UI objects
        appBar = findViewById(R.id.bottomAppBar);

        bottomNavigationView = findViewById(R.id.bottomNavBarView);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);

        home = findViewById(R.id.fab);

        // Set up Step Counter Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Set up View Pager object for different fragments
        ViewPager viewPager = findViewById(R.id.viewPager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);

        // Set up the bottom navigation tab click listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // If tab item is pressed, redirect user to the according fragment
                switch (item.getItemId()){
                    case R.id.settings:
                        viewPager.setCurrentItem(0);
                        home.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(unselectedColor)));
                        appBar.setBackgroundTint(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        appBar.setAlpha(1f);
                        return true;
                    case R.id.user:
                        viewPager.setCurrentItem(2);
                        home.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(unselectedColor)));
                        appBar.setBackgroundTint(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        appBar.setAlpha(1f);
                        return true;
                }
                return false;
            }
        });

        // Set home button click listener
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If already on camera fragment, take picture
                if (viewPager.getCurrentItem() == 1){
                    CameraFragment.captureImage();
                }
                // Else go to camera fragment
                else {
                    viewPager.setCurrentItem(1);
                    home.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF8426")));
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    appBar.setBackgroundTint(ColorStateList.valueOf(Color.parseColor("#000000")));
                    appBar.setAlpha(0.4f);
                }
            }
        });

        // Set view pager on page change listener
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // Changes colour of navigation bar based on the visited fragment
                if (position == 0){
                    home.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(unselectedColor)));
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    appBar.setBackgroundTint(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    appBar.setAlpha(1f);
                }
                if (position == 1){
                    home.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF8426")));
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    appBar.setBackgroundTint(ColorStateList.valueOf(Color.parseColor("#000000")));
                    appBar.setAlpha(0.4f);
                }
                if (position == 2){
                    UserFragment.animatePieChart();
                    home.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(unselectedColor)));
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                    appBar.setBackgroundTint(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    appBar.setAlpha(1f);
                }
            }
        });
    }

    // PagerAdopter class
    public static class MyPagerAdapter extends FragmentPagerAdapter
    {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // When screen is swiped, swaps between fragments
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SettingsFragment.newInstance();
                case 1:
                    return CameraFragment.newInstance();
                case 2:
                    return UserFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    // Sets up Step counter sensor and activates it
    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null){
            sensorManager.registerListener(this, countSensor, sensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(this, "This device does not support step counter sensor!", Toast.LENGTH_SHORT).show();
        }
    }

    // Pauses Sensor
    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    // Adds steps to counter when sensor is changed
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRunning){
            counter = event.values[0] + User.userCounter;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static float getCounter()
    {
        return counter;
    }
}