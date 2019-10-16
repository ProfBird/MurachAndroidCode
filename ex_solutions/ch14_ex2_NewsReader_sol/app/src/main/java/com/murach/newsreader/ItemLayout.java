package com.murach.newsreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ItemLayout extends RelativeLayout implements OnClickListener {

    private TextView pubDateTextView;
    private TextView titleTextView;

    private RSSItem item;
    private Context context;
    
    public ItemLayout(Context context) {   // used by Android tools
        super(context);
    }

    public ItemLayout(Context context, RSSItem i) {
        super(context);
        
        // set context and get db object
        this.context = context;

        // inflate the layout
        LayoutInflater inflater = (LayoutInflater) 
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_item, this, true);
        
        // get references to widgets
        pubDateTextView = (TextView) findViewById(R.id.pubDateTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        
        // set listeners
        this.setOnClickListener(this);

        // set task data on widgets
        setItem(i);
    }

    public void setItem(RSSItem i) {
        item = i;
        pubDateTextView.setText(item.getPubDateFormatted());
        titleTextView.setText(item.getTitle());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, ItemActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        intent.putExtra("pubdate", item.getPubDate());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("link", item.getLink());

        context.startActivity(intent);
    }
}