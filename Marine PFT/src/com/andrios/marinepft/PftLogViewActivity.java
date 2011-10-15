package com.andrios.marinepft;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class PftLogViewActivity extends Activity {
	

	protected static final int PRTACTIVITY = 0;
	protected static final int BCAACTIVITY = 1;
	static final int DATE_DIALOG_ID = 1;
	AndriosData mData;
	ArrayList<LogEntry> logList;
	int index, mMonth, mDay, mYear;
	PftEntry entry;
	TextView dateLBL;
	TextView pullupsLBL, crunchsLBL, runLBL, cardioLBL;
	TextView pullupScoreLBL, crunchScoreLBL, runScoreLBL, totalScoreLBL;
	Profile profile;
	
	Button saveBTN;
	OnClickListener myOnClickListener;
	GoogleAnalyticsTracker tracker;
	Spinner moodSpinner;
	CheckBox officialCheckBox;
	boolean changes;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pftlogviewactivity);
        
        getExtras();
        setTracker();
    }
    
	private void setTracker() {
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(this.getString(R.string.ga_api_key),
				getApplicationContext());
	}

	@Override
	public void onResume() {
		super.onResume();
		tracker.trackPageView("/" + this.getLocalClassName());
	}

	@Override
	public void onPause() {
		super.onPause();
		tracker.dispatch();
	}
    
	@SuppressWarnings("unchecked")
	private void getExtras() {
		Intent intent = this.getIntent();
		logList = (ArrayList<LogEntry>) intent.getSerializableExtra("list");
		index = intent.getIntExtra("index", -1);
		mData = (AndriosData) intent.getSerializableExtra("data");
		if(index == -1){
			Intent prtIntent = new Intent(PftLogViewActivity.this.getBaseContext(), PFTActivity.class);
			
			prtIntent.putExtra("log", true);
			prtIntent.putExtra("premium", true);
			prtIntent.putExtra("data", mData);
			startActivityForResult(prtIntent, PRTACTIVITY);
		
		}else{
			entry = (PftEntry) logList.get(index);
			changes = false;
	        setConnections();
	        setOnClickListeners();
		}
		
	}
	
	private void setConnections() {
	
		
		pullupsLBL = (TextView)findViewById(R.id.pftLogViewActivityPullupsLBL);
		pullupsLBL.setText(entry.getPullups());
		crunchsLBL = (TextView)findViewById(R.id.pftLogViewActivityCrunchsLBL);
		crunchsLBL.setText(entry.getCrunches());
		runLBL = (TextView)findViewById(R.id.pftLogViewActivityRunLBL);
		runLBL.setText(entry.getRun());

		pullupScoreLBL = (TextView)findViewById(R.id.pftLogViewActivityPullupScoreLBL);
		pullupScoreLBL.setText(entry.getPullupScore());
		crunchScoreLBL = (TextView)findViewById(R.id.pftLogViewActivityCrunchScoreLBL);
		crunchScoreLBL.setText(entry.getCrunchScore());
		runScoreLBL = (TextView)findViewById(R.id.pftLogViewActivityRunScoreLBL);
		runScoreLBL.setText(entry.getRunScore());

		totalScoreLBL = (TextView)findViewById(R.id.logViewActivityTotalScoreLBL);
		totalScoreLBL.setText(entry.getTotalScore());
		
		/*
		array_spinner=new String[8];
		array_spinner[0]="Excited";
		array_spinner[1]="Happy";
		array_spinner[2]="Scared";
		array_spinner[3]="Hopeful";
		array_spinner[4]="Worried";
		array_spinner[5]="Crying";
		array_spinner[6]="Uncertain";
		array_spinner[7]="Mad";
		moodSpinner = (Spinner) findViewById(R.id.journalEntryViewActivityMoodSpinner);
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, array_spinner);
		moodSpinner.setAdapter(adapter);
		moodSpinner.setSelection(entry.getMood());
		*/
		saveBTN = (Button) findViewById(R.id.journalEntryViewActivitySaveBTN);
		
		dateLBL = (TextView) findViewById(R.id.journalEntryViewActivityDateLBL);
		dateLBL.setText(entry.getDateString());
		
		
		officialCheckBox = (CheckBox) findViewById(R.id.pftLogViewActivityImportantCheckBox);
		officialCheckBox.setChecked(entry.isOfficial());
		setOnClickListeners();
		
	}
	
	private void setOnClickListeners(){
		officialCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				changes = true;
				
			}
			
		});
		dateLBL.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
				
			}
			
		});
		
		saveBTN.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(checkFormat()){
					Intent intent = new Intent();
			
					entry.setOfficial(officialCheckBox.isChecked());
					
					if(index == -1){
						logList.add(entry);
					}else{
						logList.set(index, entry);
					}
					
					
					intent.putExtra("list", logList);
					PftLogViewActivity.this.setResult(RESULT_OK, intent);
					PftLogViewActivity.this.finish();
				}
				
			}

			

			
			
		});
	}
	
	private void createExitDialog(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Quit Without Saving?");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				PftLogViewActivity.this.finish();
				
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}
	
	private boolean checkFormat() {
		// TODO Auto-generated method stub
		return true;
	}
	
	  @Override
	    protected Dialog onCreateDialog(int id) {
	            switch (id) {

	            case DATE_DIALOG_ID:
	                    return new DatePickerDialog(this,
	                            mDateSetListener,
	                            entry.getDate().get(Calendar.YEAR), 
	                            entry.getDate().get(Calendar.MONTH), 
	                            entry.getDate().get(Calendar.DAY_OF_MONTH));
	            }
	            return null;
	    }
	    protected void onPrepareDialog(int id, Dialog dialog) {
	            switch (id) {

	            case DATE_DIALOG_ID:
	                    ((DatePickerDialog) dialog).updateDate(
	                    		entry.getDate().get(Calendar.YEAR), 
	                            entry.getDate().get(Calendar.MONTH), 
	                            entry.getDate().get(Calendar.DAY_OF_MONTH));
	                    break;
	            }
	    }    
	   
	    private DatePickerDialog.OnDateSetListener mDateSetListener =
	            new DatePickerDialog.OnDateSetListener() {

					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						
	                    entry.setDate(dayOfMonth, monthOfYear, year);
	                    dateLBL.setText(entry.getDateString());
						
					}

	          
	    };

		@Override
	    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
			
	    	if (requestCode == PRTACTIVITY) {
	    		if (resultCode == RESULT_OK) {
	    			entry = (PftEntry) intent.getSerializableExtra("entry");
	    			setConnections();
	    			changes = true;
	    		} else {
	    			PftLogViewActivity.this.finish();
	    			
	    		}
	    	}
	    }

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event)  {
		    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		        if(changes){
		        	createExitDialog();
		        }else{
		        	PftLogViewActivity.this.finish();
		        }
		        return true;
		    }

		    return super.onKeyDown(keyCode, event);
		}
}