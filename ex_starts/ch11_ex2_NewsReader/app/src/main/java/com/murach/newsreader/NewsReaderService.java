package com.murach.newsreader;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
                    
                    // display notification
                    sendNotification("Select to view updated feed.");
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
     
    private void sendNotification(String text) {
        // create the intent for the notification
        Intent notificationIntent = new Intent(this, ItemsActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // create the pending intent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = 
                PendingIntent.getActivity(this, 0, notificationIntent, flags);
        
        // create the variables for the notification
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "Updated news feed is available";
        CharSequence contentTitle = getText(R.string.app_name);
        CharSequence contentText = text;

        // create the notification and set its data
        Notification notification = 
                new Notification.Builder(this)
            .setSmallIcon(icon)
            .setTicker(tickerText)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build();
        
        // display the notification
        NotificationManager manager = (NotificationManager) 
                getSystemService(NOTIFICATION_SERVICE);
        final int NOTIFICATION_ID = 1;
        manager.notify(NOTIFICATION_ID, notification);
    }
}