package com.murach.tasklist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TabHost;

public class TaskListFragment extends Fragment {

    private TextView taskListView;
    private String currentTabTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_list, 
            container, false);
        
        // get references to widgets
        taskListView = (TextView) view.findViewById (R.id.taskTextView);

        // get the current tab
        TabHost tabHost = (TabHost) container.getParent().getParent();
        currentTabTag = tabHost.getCurrentTabTag();
        
        // refresh the task list view
        refreshTaskList();

        // return the view
        return view;
    }
    
    public void refreshTaskList() {
        String text = "This is the " + currentTabTag + " list.";
        taskListView.setText(text);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        refreshTaskList();
    }
}