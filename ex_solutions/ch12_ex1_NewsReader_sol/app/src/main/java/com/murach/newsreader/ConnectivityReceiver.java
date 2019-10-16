package com.murach.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("News reader", "Connectivity changed");
        
        ConnectivityManager connectivityManager = (ConnectivityManager) 
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = 
                connectivityManager.getActiveNetworkInfo();

        Intent service = new Intent(context, NewsReaderService.class);
        
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("News reader", "Connected");
            context.startService(service);
        }
        else {
            Log.d("News reader", "NOT connected");
            context.stopService(service);
        }
    }
}