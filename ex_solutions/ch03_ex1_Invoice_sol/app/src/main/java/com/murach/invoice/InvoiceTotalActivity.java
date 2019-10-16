package com.murach.invoice;

import java.text.NumberFormat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class InvoiceTotalActivity extends Activity 
implements OnEditorActionListener {
	
	private EditText subtotalEditText;
	private TextView discountPercentTextView;
	private TextView discountAmountTextView;
	private TextView totalTextView;	
	
	private String subtotalString;
	
	private SharedPreferences savedValues;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice_total);
		
        subtotalEditText = (EditText) findViewById(R.id.subtotalEditText);
        discountPercentTextView = (TextView) findViewById(R.id.discountPercentTextView);
        discountAmountTextView = (TextView) findViewById(R.id.discountAmountTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        
        subtotalEditText.setOnEditorActionListener(this);        
        
    	savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);        
	}
	
	@Override
	public void onPause() {
		Editor editor = savedValues.edit();
		editor.putString("subtotalString", subtotalString);
		editor.commit();

		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

    	subtotalString = savedValues.getString("subtotalString", "");
    	subtotalEditText.setText(subtotalString);
		calculateAndDisplay();
	}

	private void calculateAndDisplay() {
		// get subtotal		
		subtotalString = subtotalEditText.getText().toString();
		float subtotal;
		if (subtotalString.equals("")) {
			subtotal = 0;	
		}
		else {
			subtotal = Float.parseFloat(subtotalString);			
		}
		
		// get discount percent
		float discountPercent = 0;
		if (subtotal >= 200) {
			discountPercent = .2f;
		}
		else if (subtotal >= 100) {
			discountPercent = .1f;
		}
		else {
			discountPercent = 0;
		}
		
		// calculate discount
		float discountAmount = subtotal * discountPercent;
		float total = subtotal - discountAmount;
		
		// display data on the layout
		NumberFormat percent = NumberFormat.getPercentInstance();		
		discountPercentTextView.setText(percent.format(discountPercent));

		NumberFormat currency = NumberFormat.getCurrencyInstance();
		discountAmountTextView.setText(currency.format(discountAmount));
		totalTextView.setText(currency.format(total));		
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {		
		calculateAndDisplay();
		
		// hide soft keyboard
		return false;
	}	
}