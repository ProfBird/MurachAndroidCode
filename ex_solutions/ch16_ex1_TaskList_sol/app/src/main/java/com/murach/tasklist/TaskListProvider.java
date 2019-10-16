package com.murach.tasklist;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class TaskListProvider extends ContentProvider{

    public static final String AUTHORITY = "com.murach.tasklist.provider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/table");
    public static final int MATCH_INT = 1;

    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "table", MATCH_INT);
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        switch(match){
            case MATCH_INT:{
                long id = new TaskListDB(getContext()).genericInsert(values);
                getContext().getContentResolver().notifyChange(uri, null);
                return uri.buildUpon().appendPath(String.valueOf(id)).build();
            }
            default:
                throw new UnsupportedOperationException(
                        "URI: " + uri + " not supported.");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        switch(match){
            case MATCH_INT:            
                return new TaskListDB(
                        getContext()).genericQuery(projection, selection, 
                        selectionArgs, sortOrder);
            default:
                throw new UnsupportedOperationException (
                        "URI " + uri + " is not supported.");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch(match){
        case MATCH_INT:
            int n = new TaskListDB(getContext()).genericUpdate(
                            values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return n;
        default:
            throw new UnsupportedOperationException (
                    "URI " + uri + " is not supported.");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch(match){
        case MATCH_INT:
            int n = new TaskListDB(getContext()).genericDelete(selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return n;
        default:
            throw new UnsupportedOperationException ("URI " + uri + " is not supported.");
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch(match) {
        case MATCH_INT:
            return "vnd.android.cursor.dir/vnd.com.murach.tasklist.provider";
        default:
            throw new UnsupportedOperationException ("URI " + uri + " is not supported.");
        }
    }
}