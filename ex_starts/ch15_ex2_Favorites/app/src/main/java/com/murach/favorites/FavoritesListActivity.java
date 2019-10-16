package com.murach.favorites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FavoritesListActivity extends Activity implements OnItemClickListener {
    
    private ListView favoritesListView;
    
    private final Uri DATA_URI = Data.CONTENT_URI;
    
    // these are tied to the columns
    private final int NAME_COLUMN = 1;
    private final int PHONE_NUMBER_COLUMN = 2;
    private final int PHONE_TYPE_COLUMN = 3;
    
    private Cursor cursor;

    private SimpleCursorAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        
        // get reference to widget
        favoritesListView = (ListView) findViewById(R.id.favoritesListView);
        
        // set listeners
        favoritesListView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    
        // set up variables for the cursor
        String[] columns = {
                Data._ID,              // primary key
                Contacts.DISPLAY_NAME, // person's name
                Data.DATA1,            // phone number
                Data.DATA2             // phone label (home, mobile, work, etc.)
        };
        String where = 
                Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND " + 
                Contacts.STARRED + "='1'";
        String orderBy = Contacts.TIMES_CONTACTED + " DESC";
        
        // get the cursor
        cursor = getContentResolver().query(
                DATA_URI, columns, where, null, orderBy);
        
        // set up variables for the adapter
        int layoutId = R.layout.favorite;
        String[] fromColumns = { 
            Contacts.DISPLAY_NAME,
            Data.DATA2,
            Data.DATA1
        };
        int[] toViews = {
            R.id.nameTextView,
            R.id.labelTextView,
            R.id.numberTextView
        };
        
        // get the adapter for the cursor
        adapter = new SimpleCursorAdapter(this, layoutId, 
                cursor, fromColumns, toViews, 0);
        
        // convert int values from the DATA2 column to readable values
        // See http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone.html
        adapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int colIndex) {
                if (colIndex == PHONE_TYPE_COLUMN){
                    int phoneType = cursor.getInt(colIndex);
                    TextView tv = (TextView) view;
                    switch (phoneType){
                    case 1:
                        tv.setText("Home");
                        return true;
                    case 2:
                        tv.setText("Mobile");
                        return true;
                    case 3:
                        tv.setText("Work");
                        return true;
                    default :
                        tv.setText("Other");
                        return true;
                    }
                }
                return false;
            }
        });
        
        favoritesListView.setAdapter(adapter);
    }
    
    private void makeCall(CharSequence phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel: "+ phoneNumber));
        startActivity(callIntent);
    }

    private void makeText(CharSequence phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        startActivity(intent);    
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        // Get the name and phone number of the contact
        cursor.moveToPosition(position);
        final String name = cursor.getString(NAME_COLUMN);
        final CharSequence phoneNumber = cursor.getString(PHONE_NUMBER_COLUMN);
        
        // Display a dialog to call or text the contact
        new AlertDialog.Builder(this)
        .setMessage("Would you like to text or call " + name + 
                " (" + phoneNumber + ") " + " ?")
        .setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                makeCall(phoneNumber);
            }
        })
        .setNeutralButton("Text", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeText(phoneNumber);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        })
        .show();
    }
}