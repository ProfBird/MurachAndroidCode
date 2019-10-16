package com.murach.tasklist;

import java.util.ArrayList;

import com.google.tabmanager.TabManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TaskListActivity extends FragmentActivity {
    TabHost tabHost;
    TabManager tabManager;
    TaskListDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        
        // get tab manager
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();        
        tabManager = new TabManager(this, tabHost, R.id.realtabcontent);
        
        // get database
        db = new TaskListDB(getApplicationContext());

        // add a tab for each list in the database
        ArrayList<List> lists = db.getLists();
        if (lists != null && lists.size() > 0) {
            for (List list : lists) {
                TabSpec tabSpec = tabHost.newTabSpec(list.getName());
                tabSpec.setIndicator(list.getName());
                tabManager.addTab(tabSpec, TaskListFragment.class, null);
            }
        }

        // sets current tab to the last tab opened
        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_task_list, menu);
        return true;
    }    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuAddTask:
                Intent intent = new Intent(this, AddEditActivity.class);
                intent.putExtra("tab", tabHost.getCurrentTabTag());
                startActivity(intent);
                break;
            case R.id.menuDelete:
                // Hide all tasks marked as complete
                ArrayList<Task> tasks = db.getTasks(tabHost.getCurrentTabTag());
                for (Task task : tasks){
                    if (task.getCompletedDateMillis() > 0){
                        task.setHidden(Task.TRUE);
                        db.updateTask(task);
                    }
                }
                
                // Refresh list
                TaskListFragment currentFragment = (TaskListFragment) 
                        getSupportFragmentManager().
                        findFragmentByTag(tabHost.getCurrentTabTag());
                currentFragment.refreshTaskList();
                
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}