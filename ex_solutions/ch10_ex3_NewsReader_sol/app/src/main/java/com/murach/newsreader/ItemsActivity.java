package com.murach.newsreader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ItemsActivity extends Activity 
implements OnItemClickListener {

    private final String URL_STRING = "http://rss.cnn.com/rss/cnn_world.rss";
    private final String FILENAME = "news_feed.xml";
    
    private RSSFeed feed;
    
    private TextView titleTextView;
    private TextView pubDateTextView;
    private ListView itemsListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        pubDateTextView = (TextView) findViewById(R.id.pubDateTextView);
        itemsListView = (ListView) findViewById(R.id.itemsListView);
        
        itemsListView.setOnItemClickListener(this);
        
        new DownloadFeed().execute();
    }
    
    class DownloadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
        
            try{
                // get the URL
                URL url = new URL(URL_STRING);
    
                // get the input stream
                InputStream in = url.openStream();
                
                // get the output stream
                Context context = ItemsActivity.this;
                FileOutputStream out = 
                    context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
    
                // read input and write output
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                while (bytesRead != -1)
                {
                    out.write(buffer, 0, bytesRead);
                    bytesRead = in.read(buffer);
                }
                out.close();
                in.close();
            } 
            catch (IOException e) {
                Log.e("News reader", e.toString());
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed downloaded: " + new Date());
            new ReadFeed().execute();
        }
    }
    
    class ReadFeed extends AsyncTask<Void, Void, Void> {
        
        @Override
        protected Void doInBackground(Void... params) {
            try {
                // get the XML reader
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader xmlreader = parser.getXMLReader();
    
                // set content handler
                RSSFeedHandler theRssHandler = new RSSFeedHandler();
                xmlreader.setContentHandler(theRssHandler);
    
                // read the file from internal storage
                FileInputStream in = openFileInput(FILENAME);
    
                // parse the data
                InputSource is = new InputSource(in);
                xmlreader.parse(is);
    
                // set the feed in the activity
                ItemsActivity.this.feed = theRssHandler.getFeed();
            } 
            catch (Exception e) {
                Log.e("News reader", e.toString());
            }
            return null;
        }
        
        // This is executed after the feed has been read
        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed read: " + new Date());
            
            // update the display for the activity
            ItemsActivity.this.updateDisplay();
        }
    }
    
    public void updateDisplay()
    {
        if (feed == null) {
            titleTextView.setText("Unable to get RSS feed");
            return;
        }

        // set the title and pub date for the feed
        titleTextView.setText(feed.getTitle());
        pubDateTextView.setText(feed.getPubDateFormatted());
        
        // get the items for the feed
        ArrayList<RSSItem> items = feed.getAllItems();

        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data = 
                new ArrayList<HashMap<String, String>>();
        for (RSSItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("date", item.getPubDateFormatted());
            map.put("title", item.getTitle());
            map.put("description", item.getDescription());
            data.add(map);
        }
        
        // create the resource, from, and to variables 
        int resource = R.layout.listview_item;
        String[] from = {"date", "title", "description"};
        int[] to = {R.id.pubDateTextView, R.id.titleTextView, R.id.descriptionTextView};

        // create and set the adapter
        SimpleAdapter adapter = 
            new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);
        
        Log.d("News reader", "Feed displayed: " + new Date());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, 
            int position, long id) {

        // get the item at the specified position
        RSSItem item = feed.getItem(position);

        // get the Uri for the link
        String link = item.getLink();
        Uri viewUri = Uri.parse(link);
        
        // create the intent and start it
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, viewUri); 
        startActivity(viewIntent);
    }
}