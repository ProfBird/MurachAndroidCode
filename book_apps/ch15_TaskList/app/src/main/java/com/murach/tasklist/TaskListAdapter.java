package com.murach.tasklist;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TaskListAdapter extends BaseAdapter {
    
    private Context context;
    private ArrayList<Task> tasks;

    public TaskListAdapter(Context context, ArrayList<Task> tasks){
        this.context = context;
        this.tasks = tasks;
    }
    
    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskLayout taskLayout = null;
        Task task = tasks.get(position);
        
        if (convertView == null) {
            taskLayout = new TaskLayout(context, task);
        }
        else {
            taskLayout = (TaskLayout) convertView;
            taskLayout.setTask(task);
        }
        return taskLayout;
    }
}