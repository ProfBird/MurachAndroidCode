package com.murach.newsreader;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ItemsAdapter extends BaseAdapter {
    
    private Context context;
    List<RSSItem> items;

    public ItemsAdapter(Context context, List<RSSItem> items){
        this.context = context;
        this.items = items;
    }
    
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemLayout itemLayout = null;
        RSSItem item = items.get(position);
        
        if (convertView == null) {
            itemLayout = new ItemLayout(context, item);
        }
        else {
            itemLayout = (ItemLayout) convertView;
            itemLayout.setItem(item);
        }
        return itemLayout;
    }
}