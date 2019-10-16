package com.murach.newsreader;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

public class RSSFeedHandler extends DefaultHandler {
    RSSFeed feed;
    RSSItem item;
    
    boolean feedTitleHasBeenRead = false;
    boolean feedPubDateHasBeenRead = false;
    
    boolean isTitle = false;
    boolean isDescription = false;
    boolean isLink = false;
    boolean isPubDate = false;
    
    RSSFeedHandler() {}
    
    public RSSFeed getFeed() {
        return feed;
    }
        
    public void startDocument() throws SAXException {
        feed = new RSSFeed();
        item = new RSSItem(); // an item to temporarily store feed data
    }
    
    public void endDocument() throws SAXException { }
    
    public void startElement(String namespaceURI, String localName, 
            String qName, Attributes atts) throws SAXException {
        
        if (qName.equals("item")) {
            // create a new item
            item = new RSSItem();
            return;
        }
        else if (qName.equals("title")) {
            isTitle = true;
            return;
        }
        else if (qName.equals("description")) {
            isDescription = true;
            return;
        }
        else if (qName.equals("link")) {
            isLink = true;
            return;
        }
        else if (qName.equals("pubDate")) {
            isPubDate = true;
            return;
        }
    }
    
    public void endElement(String namespaceURI, String localName, 
            String qName) throws SAXException
    {
        if (qName.equals("item")) {
            feed.addItem(item);
            return;
        }
    }
     
    public void characters(char ch[], int start, int length)
    {
        String s = new String(ch, start, length);
        // Log.i("RSSHandler", "s: " + s);
        
        if (isTitle) {
            if (feedTitleHasBeenRead == false) {
                feed.setTitle(s);
                feedTitleHasBeenRead = true;
            } 
            else {
                item.setTitle(s);
            }
            isTitle = false;
        }
        else if (isLink) {
            item.setLink(s);
            isLink = false;
        }
        else if (isDescription) {
            item.setDescription(s);
            isDescription = false;
        }
        else if (isPubDate) {
            if (feedPubDateHasBeenRead == false) {
                feed.setPubDate(s);
                feedPubDateHasBeenRead = true;
            }
            else {
                item.setPubDate(s);
            }
            isPubDate = false;
        }        
    }
}