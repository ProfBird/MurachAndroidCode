package com.murach.newsreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ItemsActivity extends Activity 
implements OnItemClickListener {

    private NewsReaderApp app;
    private RSSFeed feed;
    private long feedPubDateMillis;
    private FileIO io;
    
    private TextView titleTextView;
    private ListView itemsListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        itemsListView = (ListView) findViewById(R.id.itemsListView);
        
        itemsListView.setOnItemClickListener(this);
        
        // get references to Application and FileIO objects
        app = (NewsReaderApp) getApplication();
        io = new FileIO(getApplicationContext());
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get feed from app object
        feedPubDateMillis = app.getFeedMillis();
        
        if (feedPubDateMillis == -1) {
            new DownloadFeed().execute();   // download, read, and display
        }
        else if (feed == null) {
            new ReadFeed().execute();       // read and display
        }
        else {
            updateDisplay();                // just display
        }
    }
    
    class DownloadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            io.downloadFile();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed downloaded");
            new ReadFeed().execute();
        }
    }
    
    class ReadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            feed = io.readFile(); 
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed read");
            app.setFeedMillis(feed.getPubDateMillis());
            ItemsActivity.this.updateDisplay();
        }
    }
    
    public void updateDisplay()
    {
        if (feed == null) {
            titleTextView.setText("Unable to get RSS feed");
            return;
        }

        // set the title for the feed
        titleTextView.setText(feed.getTitle());
        
        // get the items for the feed
        List<RSSItem> items = feed.getAllItems();

        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data = 
                new ArrayList<HashMap<String, String>>();
        for (RSSItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("date", item.getPubDateFormatted());
            map.put("title", item.getTitle());
            data.add(map);
        }
        
        // create the resource, from, and to variables 
        int resource = R.layout.listview_item;
        String[] from = {"date", "title"};
        int[] to = { R.id.pubDateTextView, R.id.titleTextView};

        // create and set the adapter
        SimpleAdapter adapter = 
                new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);
        
        Log.d("News reader", "Feed displayed");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, 
            int position, long id) {
        RSSItem item = feed.getItem(position);
        
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("pubdate", item.getPubDate());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("link", item.getLink());

        this.startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_items, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                new DownloadFeed().execute();
                Toast.makeText(this, "Feed refreshed!", 
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}