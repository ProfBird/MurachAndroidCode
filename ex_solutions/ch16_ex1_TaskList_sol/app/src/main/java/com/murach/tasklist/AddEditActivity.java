package com.murach.tasklist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AddEditActivity extends Activity 
implements OnKeyListener {
    
    private EditText nameEditText;
    private EditText notesEditText;
    private Spinner listSpinner;

    private TaskListDB db;
    private boolean editMode;
    private String currentTabName = "";
    private Task task;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_add_edit);
        
        // get references to widgets
        listSpinner = (Spinner) findViewById(R.id.listSpinner);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        notesEditText = (EditText) findViewById(R.id.notesEditText);
        
        // set listeners
        nameEditText.setOnKeyListener(this);
        notesEditText.setOnKeyListener(this);

        // get the database object
        db = new TaskListDB(this);

        // set the adapter for the spinner
        ArrayList<List> lists = db.getLists();
        ArrayAdapter<List> adapter = new ArrayAdapter<List>(
                this, R.layout.spinner_list, lists);
        listSpinner.setAdapter(adapter);

        // get edit mode from intent
        Intent intent = getIntent();
        editMode = intent.getBooleanExtra("editMode", false);
        
        // if editing
        if (editMode) {
            // get task
            long taskId = intent.getLongExtra("taskId", -1);
            task = db.getTask(taskId);
            
            // update UI with task
            nameEditText.setText(task.getName());
            notesEditText.setText(task.getNotes());
        }
        
        // set the correct list for the spinner
        long listID;
        if (editMode) {   // edit mode - use same list as selected task
            listID = (int) task.getListId();
        }
        else {            // add mode - use the list for the current tab
            currentTabName = intent.getStringExtra("tab");
            listID = (int) db.getList(currentTabName).getId();
        }
        // subtract 1 from database ID to get correct list position
        int listPosition = (int) listID - 1;
        listSpinner.setSelection(listPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_edit, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSave:
                saveToDB();
                this.finish();
                break;
            case R.id.menuCancel:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void saveToDB() {
        // get data from widgets
        int listID = listSpinner.getSelectedItemPosition() + 1;
        String name = nameEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        
        // if no task name, exit method
        if (name == null || name.equals("")) {
            return;
        }
        
        // if add mode, create new task
        if (!editMode) {
            task = new Task();
        }
        
        // put data in task 
        task.setListId(listID);
        task.setName(name);
        task.setNotes(notes);
        
        // update or insert task
        if (editMode) {
            db.updateTask(task);
        }
        else {
            db.insertTask(task);
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // hide the soft Keyboard
            InputMethodManager imm = (InputMethodManager) 
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveToDB();
            return false;
        }
        return false;
    }
}