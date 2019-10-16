package com.murach.tasklist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AppWidgetTaskCount extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, 
            AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // loop through all app widgets for this provider
        for (int i = 0; i < appWidgetIds.length; i++) {
            
            // create a pending intent for the Task List activity
            Intent intent = new Intent(context, TaskListActivity.class);
            PendingIntent pendingIntent = 
                    PendingIntent.getActivity(context, 0, intent, 0);

            // get the layout and set the listener for the app widget
            RemoteViews views = new RemoteViews(
                    context.getPackageName(), R.layout.app_widget_task_count);
            views.setOnClickPendingIntent(
                    R.id.appwidget_task_count, pendingIntent);
            
            // get the names to display on the app widget
            TaskListDB db = new TaskListDB(context);
            int taskCount = db.getTaskCount();
            
            // update the user interface
            views.setTextViewText(R.id.taskCountTextView, 
                    Integer.toString(taskCount));
            
            // update the current app widget
            int appWidgetId = appWidgetIds[i];
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        if (intent.getAction().equals(TaskListDB.TASK_MODIFIED)) {
            AppWidgetManager manager = 
                    AppWidgetManager.getInstance(context);
            ComponentName provider = 
                    new ComponentName(context, AppWidgetTaskCount.class);
            int[] appWidgetIds = manager.getAppWidgetIds(provider);
            onUpdate(context, manager, appWidgetIds);
        }
    }
}