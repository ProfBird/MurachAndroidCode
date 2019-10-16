package com.murach.tasklist;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TabHost;

public class TaskListFragment extends Fragment {

    private TextView taskTextView;
    private ListView taskListView;
    private String currentTabTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_list, 
            container, false);
        
        // get references to widgets
        taskTextView = (TextView) view.findViewById (R.id.taskTextView);
        taskListView = (ListView) view.findViewById (R.id.taskListView);

        // get the current tab
        TabHost tabHost = (TabHost) container.getParent().getParent();
        currentTabTag = tabHost.getCurrentTabTag();
        
        // refresh the task list view
        refreshTaskList();

        // return the view
        return view;
    }
    
    public void refreshTaskList() {
        // get the database object
        TaskListDB db = new TaskListDB(getActivity().getApplicationContext());
        
        // get the tasks
        ArrayList<Task> tasks = db.getTasks(currentTabTag);
        
        // build the string for the tasks
        String text = "";
        for (Task task : tasks) {
            text += task.getName() + "\n";
        }
        
        // display the string on the user interface
        taskTextView.setText(text);
        
        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data = 
                new ArrayList<HashMap<String, String>>();
        for (Task task : tasks) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", task.getName());
            map.put("notes", task.getNotes());
            data.add(map);
        }
        
        // create the resource, from, and to variables 
        int resource = R.layout.listview_task;
        String[] from = {"name", "notes"};
        int[] to = { R.id.nameTextView, R.id.notesTextView};

        // create and set the adapter
        SimpleAdapter adapter = 
                new SimpleAdapter(getActivity(), data, resource, from, to);
        taskListView.setAdapter(adapter);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        refreshTaskList();
    }
}