package com.murach.tasklist;

import java.util.ArrayList;

import com.google.tabmanager.TabManager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TaskListActivity extends FragmentActivity {
    TabHost tabHost;
    TabManager tabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        
        // get tab manager
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();        
        tabManager = new TabManager(this, tabHost, R.id.realtabcontent);
        
        // get the lists from the database
        TaskListDB db = new TaskListDB(this);
        ArrayList<List> lists = db.getLists();
        
        // add a tab for each list
        if (lists != null && ! lists.isEmpty()) {
            for (List list : lists) {
                TabSpec tabSpec = tabHost.newTabSpec(list.getName());
                tabSpec.setIndicator(list.getName());
                tabManager.addTab(tabSpec, TaskListFragment.class, null);
            }
        }

        // set current tab to the last tab opened
        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }
}