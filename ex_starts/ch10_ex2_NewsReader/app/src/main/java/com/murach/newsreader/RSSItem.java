package com.murach.newsreader;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class RSSItem {
    
    private String title = null;
    private String description = null;
    private String link = null;
    private String pubDate = null;
    
    private SimpleDateFormat dateOutFormat = 
        new SimpleDateFormat("EEEE h:mm a (MMM d)");
    
    private SimpleDateFormat dateInFormat = 
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
    
    public void setTitle(String title)     {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setDescription(String description)     {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getLink() {
        return link;
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
}