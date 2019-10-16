package com.murach.newsreader;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class RSSFeed {
    public final static String NEW_FEED = "com.murach.newsreader.NEW_FEED";
    
    private String title = null;
    private String pubDate = null;
    private ArrayList<RSSItem> items;
        
    private SimpleDateFormat dateInFormat = 
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        
    public RSSFeed() {
        items = new ArrayList<RSSItem>(); 
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
    
    public String getPubDate() {
        return pubDate;
    }
    
    public int addItem(RSSItem item) {
        items.add(item);
        return items.size();
    }
    
    public RSSItem getItem(int index) {
        return items.get(index);
    }
    
    public ArrayList<RSSItem> getAllItems() {
        return items;
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public long getPubDateMillis() {
        Date date = new Date(0);
        try {
            date = dateInFormat.parse(pubDate.trim());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long dateMillis = date.getTime();
        return dateMillis;
    }
}