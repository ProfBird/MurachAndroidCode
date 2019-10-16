package com.murach.newsreader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class RSSFeed {
    private String title = null;
    private String pubDate = null;
    private ArrayList<RSSItem> items;
        
    private SimpleDateFormat dateInFormat = 
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        
    private SimpleDateFormat dateOutFormat = 
            new SimpleDateFormat("EEEE h:mm a (MMM d)");
        
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
    
    public String getPubDateFormatted() {
        try {
            Date date = dateInFormat.parse(pubDate.trim());
            String pubDateFormatted = dateOutFormat.format(date);
            return pubDateFormatted;
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public long getPubDateMillis() {
        try {
            Date date = dateInFormat.parse(pubDate.trim());
            return date.getTime();
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
}