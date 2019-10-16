package com.murach.tester;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TesterService extends Service {
    
    Timer timer;
    
    @Override
    public void onCreate() {
        Log.d("Tester service", "Service created");
        // startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Tester service", "Service started");
        // Log.d("Tester service", "Task completed");
        // stopSelf();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Tester service", "Service bound");
        return null;
    }
    
    @Override
    public void onDestroy() {
        Log.d("Tester service", "Service destroyed");
        // stopTimer();
    }
    
    @SuppressWarnings("unused")
    private void startTimer() {
        // create task
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d("News reader", "Timer task executed");
            }
        };
        
        // create and start timer
        timer = new Timer(true);
        int delay = 1000 * 1;      // 1 second
        int interval = 1000 * 5;   // 5 seconds
        timer.schedule(task, delay, interval);
    }
    
    @SuppressWarnings("unused")
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}