package com.murach.tipcalculator;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment 
implements OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }
    
    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, 
            String key) {
        TipCalculatorFragment tipFragment = 
            (TipCalculatorFragment) getFragmentManager()
                .findFragmentById(R.id.main_fragment);
        if (tipFragment != null) {
            tipFragment.onResume();
        }
        
    }
}