package com.murach.taskhistory;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskHistoryActivity extends Activity
implements OnItemClickListener {
    
    public static final String TASK_ID = "_id";
    public static final int    TASK_ID_COL = 0;
    public static final String TASK_NAME = "task_name";
    public static final int    TASK_NAME_COL = 2;
    public static final String TASK_NOTES = "notes";
    public static final int    TASK_NOTES_COL = 3;
    public static final String TASK_COMPLETED = "date_completed";
    public static final int    TASK_COMPLETED_COL = 4;
    public static final String TASK_HIDDEN = "hidden";
    public static final int    TASK_HIDDEN_COL = 5;
    
    public static final String AUTHORITY = "com.murach.tasklist.provider";
    public static final Uri TASKS_URI = 
            Uri.parse("content://" + AUTHORITY + "/tasks");
    
    private ListView taskListView;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private Context context;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        
        context = this;
        taskListView = (ListView) findViewById(R.id.taskListView);
    }
    
    @Override
    public void onResume() {
        super.onResume();

        // get cursor
        String where = TASK_HIDDEN + "= '1' ";
        String orderBy = TASK_COMPLETED + " DESC";
        cursor = getContentResolver()
                .query(TASKS_URI, null, where, null, orderBy);
        
        // define variables for adapter
        int layout_id = R.layout.listview_task;
        String[] fromColumns = {TASK_NAME, TASK_NOTES, 
            TASK_COMPLETED};
        int[] toViews = {R.id.nameTextView, R.id.notesTextView,
            R.id.dateTextView};
        
        // create and set adapter
        adapter = new SimpleCursorAdapter(this, layout_id, 
                cursor, fromColumns, toViews, 0);
        taskListView.setAdapter(adapter);
        taskListView.setOnItemClickListener(this);

        // convert column data to readable values
        adapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, 
                    int colIndex) {
                if (colIndex == TASK_COMPLETED_COL){
                    long dateMillis = cursor.getLong(colIndex);
                    TextView tv = (TextView) view;
                    SimpleDateFormat date = 
                            new SimpleDateFormat("EEE, MMM d yyyy HH:mm");
                    tv.setText("Completed on: " + 
                            date.format(new Date(dateMillis)));
                    return true;
                } 
                else if (colIndex == TASK_NOTES_COL) {
                    String notes = cursor.getString(colIndex);
                    if (notes == null || notes.equals("")) {
                        TextView tv = (TextView) view;
                        tv.setText("No notes");
                        return true;
                    }
                }
                return false;
            }
        });
    }
    
    @Override
    protected void onPause() {
        adapter.changeCursor(null);   // close cursor for the adapter
        cursor.close();               // close cursor for the activity
        super.onPause();
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, 
            int position, long id) {

        // get data from cursor
        cursor.moveToPosition(position);
        final int taskId = cursor.getInt(TASK_ID_COL);
        final String taskName = cursor.getString(TASK_NAME_COL);
        
        // display a dialog to confirm the delete
        new AlertDialog.Builder(this)
        .setMessage("Do you want to permanently delete task: " + 
                taskName + "?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // delete the specified task
                String where = TASK_ID + " = ?";
                String[] whereArgs = { Integer.toString(taskId) };
                int deleteCount = getContentResolver()
                        .delete(TASKS_URI, where, whereArgs);
                if (deleteCount <= 0) {
                    Toast.makeText(context, "Delete operation failed", 
                        Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Delete operation successful", 
                        Toast.LENGTH_SHORT).show();
                    onResume();
                }
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        })
        .show();
    }
}