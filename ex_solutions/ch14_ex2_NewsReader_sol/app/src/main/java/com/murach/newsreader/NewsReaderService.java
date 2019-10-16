package com.murach.newsreader;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NewsReaderService extends Service {

    private NewsReaderApp app;
    private Timer timer;
    private FileIO io;
    
    @Override
    public void onCreate() {
        Log.d("News reader", "Service created");
        app = (NewsReaderApp) getApplication();
        io = new FileIO(getApplicationContext());
        startTimer();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("News reader", "Service started");
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("News reader", "Service bound - not used!");
        return null;
    }
    
    @Override
    public void onDestroy() {
        Log.d("News reader", "Service destroyed");
        stopTimer();
    }
    
    private void startTimer() {
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                Log.d("News reader", "Timer task started");
                
                io.downloadFile();
                Log.d("News reader", "File downloaded");
                
                RSSFeed newFeed = io.readFile();
                Log.d("News reader", "File read");
                
                // if new feed is newer than old feed
                if (newFeed.getPubDateMillis() > app.getFeedMillis()) {
                    Log.d("News reader", "Updated feed available.");
                    
                    // update app object
                    app.setFeedMillis(newFeed.getPubDateMillis());
                }
                else {
                    Log.d("News reader", "Updated feed NOT available.");
                }
                
            }
        };
        
        timer = new Timer(true);
        int delay = 1000 * 60 * 60;      // 1 hour
        int interval = 1000 * 60 * 60;   // 1 hour
        timer.schedule(task, delay, interval);
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}