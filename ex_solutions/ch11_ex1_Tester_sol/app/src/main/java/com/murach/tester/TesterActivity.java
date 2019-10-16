package com.murach.tester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TesterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
        
        Intent serviceIntent = new Intent(this, TesterService.class);
        startService(serviceIntent);
    }
}
