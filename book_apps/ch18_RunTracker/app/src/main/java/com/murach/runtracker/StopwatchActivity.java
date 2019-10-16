package com.murach.runtracker;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StopwatchActivity extends Activity implements OnClickListener {

    private TextView hoursTextView;
    private TextView minsTextView;
    private TextView secsTextView;
    private TextView tenthsTextView;

    private Button resetButton;
    private Button startStopButton;
    private Button mapButton;
    
    private long startTimeMillis;
    private long elapsedTimeMillis;

    private int elapsedHours;
    private int elapsedMins;
    private int elapsedSecs;
    private int elapsedTenths;

    private Timer timer;
    private NumberFormat number;
    
    private SharedPreferences prefs;
    private boolean stopwatchOn;

    private Intent serviceIntent;
    
    private final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        
        // get references to widgets
        hoursTextView = (TextView) findViewById(R.id.textViewHoursValue);
        minsTextView = (TextView) findViewById(R.id.textViewMinsValue);
        secsTextView = (TextView) findViewById(R.id.textViewSecsValue);
        tenthsTextView = (TextView) findViewById(R.id.textViewTenthsValue);
        resetButton = (Button) findViewById(R.id.buttonReset);
        startStopButton = (Button) findViewById(R.id.buttonStartStop);
        mapButton = (Button) findViewById(R.id.buttonViewMap);
        
        // set listeners
        resetButton.setOnClickListener(this);
        startStopButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);

        // get preferences
        prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        
        // create intents
        serviceIntent = new Intent(this, RunTrackerService.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        Editor edit = prefs.edit();
        edit.putBoolean("stopwatchOn", stopwatchOn);
        edit.putLong("startTimeMillis", startTimeMillis);
        edit.putLong("elapsedTimeMillis", elapsedTimeMillis);
        edit.commit();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        stopwatchOn = prefs.getBoolean("stopwatchOn", false);
        startTimeMillis = prefs.getLong("startTimeMillis", System.currentTimeMillis());
        elapsedTimeMillis = prefs.getLong("elapsedTimeMillis", 0);

        if (stopwatchOn) {
            start();
        }
        else {
            updateViews(elapsedTimeMillis);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStartStop:
                if (stopwatchOn) {
                    stop();
                }
                else {
                    start();
                }        
                break;
            case R.id.buttonReset:
                reset();
                break;
            case R.id.buttonViewMap:
                Intent runMap = new Intent(this, RunMapActivity.class);
                startActivity(runMap);
                break;
        }
    }

    private void start() {
        // make sure old timer thread has been cancelled 
        if (timer != null) {
            timer.cancel();
        }

        // if stopped or reset, set new start time
        if (stopwatchOn == false) {
            startTimeMillis = System.currentTimeMillis() - elapsedTimeMillis;
        }
        
        // update variables and UI
        stopwatchOn = true;
        startStopButton.setText(R.string.stop);
        
        // if GPS is not enabled, start GPS settings activity
        LocationManager locationManager = 
                (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "Please activate GPS settings",
                    Toast.LENGTH_LONG).show();
            Intent intent = 
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        
        // start service
        startService(serviceIntent);
        startNotification();

        // start new timer thread
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
                updateViews(elapsedTimeMillis);
            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(task, 0, 100);
    }
    
    private void stop() {
        // stop timer
        stopwatchOn = false;
        if (timer != null) {
            timer.cancel();
        }
        startStopButton.setText(R.string.start);
        
        // stop service
        stopService(serviceIntent);
        stopNotification();
        
        // update views
        updateViews(elapsedTimeMillis);    
    }

    private void reset() {
        // stop timer
        this.stop();
        
        // clear the list of locations in the database
        RunTrackerDB db = new RunTrackerDB(this);
        db.deleteLocations();
        
        // reset millis and update views
        elapsedTimeMillis = 0;
        updateViews(elapsedTimeMillis);
    }
    
    private void updateViews(final long elapsedMillis) {
        elapsedTenths = (int) ((elapsedMillis/100) % 10);
        elapsedSecs = (int) ((elapsedMillis/1000) % 60);
        elapsedMins = (int) ((elapsedMillis/(60*1000)) % 60);
        elapsedHours = (int) (elapsedMillis/(60*60*1000));

        if (elapsedHours > 0) {
            updateView(hoursTextView, elapsedHours, 1);
        }
        updateView(minsTextView, elapsedMins, 2);
        updateView(secsTextView, elapsedSecs, 2);
        updateView(tenthsTextView, elapsedTenths, 1);
    }
    
    private void updateView(final TextView textView,
            final long elapsedTime, final int minIntDigits) {

        // post changes to UI thread
        number = NumberFormat.getInstance();
        textView.post(new Runnable() {

            @Override
            public void run() {
                number.setMinimumIntegerDigits(minIntDigits);
                textView.setText(number.format(elapsedTime));
            }
        });
    }
    
    private void startNotification() {
        notificationManager = (NotificationManager) 
                getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = 
                new Intent(this, StopwatchActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = 
                PendingIntent.getActivity(this, 0, notificationIntent, flags);
        
        int icon = R.drawable.ic_launcher;
        Notification notification= 
                new NotificationCompat.Builder(getApplicationContext())
           .setSmallIcon(icon)
           .setTicker(getText(R.string.app_name))
           .setContentTitle(getText(R.string.app_name))
           .setContentText(getText(R.string.content_text))
           .setContentIntent(pendingIntent)
           .build();
        
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    private void stopNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
}