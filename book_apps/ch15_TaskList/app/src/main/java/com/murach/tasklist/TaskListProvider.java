package com.murach.tasklist;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class TaskListProvider extends ContentProvider {

    public static final String AUTHORITY = "com.murach.tasklist.provider";
    
    public static final int NO_MATCH = -1;
    public static final int ALL_TASKS_URI = 0;
    public static final int SINGLE_TASK_URI = 1;
    
    private TaskListDB db;
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        db = new TaskListDB(getContext());

        uriMatcher = new UriMatcher(NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "tasks", ALL_TASKS_URI);
        uriMatcher.addURI(AUTHORITY, "tasks/#", SINGLE_TASK_URI);
        
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String where,
            String[] whereArgs, String orderBy) {
        switch(uriMatcher.match(uri)) {
            case ALL_TASKS_URI:
                return db.queryTasks(columns, where, 
                        whereArgs, orderBy);
            default:
                throw new UnsupportedOperationException (
                        "URI " + uri + " is not supported.");
        }
    }

    @Override
    public String getType(Uri uri) {
        switch(uriMatcher.match(uri)) {
            case ALL_TASKS_URI:
                return "vnd.android.cursor.dir/vnd.murach.tasklist.tasks";
            case SINGLE_TASK_URI:
                return "vnd.android.cursor.item/vnd.murach.tasklist.tasks";
            default:
                throw new UnsupportedOperationException(
                        "URI " + uri + " is not supported.");
        }
    }
    
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch(uriMatcher.match(uri)) {
            case ALL_TASKS_URI:
                long insertId = db.insertTask(values);
                getContext().getContentResolver().notifyChange(uri, null);
                return uri.buildUpon().appendPath(
                        Long.toString(insertId)).build();
            default:
                throw new UnsupportedOperationException(
                        "URI: " + uri + " is not supported.");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
            String[] whereArgs) {
        int updateCount;
        switch(uriMatcher.match(uri)) {
            case SINGLE_TASK_URI:
                String taskId = uri.getLastPathSegment();
                String where2 = "_id= ? ";
                String[] whereArgs2 = { taskId };
                updateCount = db.updateTask(values, where2, whereArgs2);
                getContext().getContentResolver().notifyChange(uri, null);
                return updateCount;
            case ALL_TASKS_URI:
                updateCount = db.updateTask(values, where, whereArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return updateCount;
            default:
                throw new UnsupportedOperationException (
                        "URI " + uri + " is not supported.");
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int deleteCount;
        switch(uriMatcher.match(uri)) {
            case SINGLE_TASK_URI:
                String taskId = uri.getLastPathSegment();
                String where2 = "_id = ?";
                String[] whereArgs2 = { taskId };
                deleteCount = db.deleteTask(where2, whereArgs2);
                getContext().getContentResolver().notifyChange(uri, null);
                return deleteCount;
            case ALL_TASKS_URI:
                deleteCount = db.deleteTask(where, whereArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return deleteCount;
            default:
                throw new UnsupportedOperationException (
                        "URI " + uri + " is not supported.");
        }
    }
}