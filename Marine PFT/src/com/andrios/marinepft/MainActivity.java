package com.andrios.marinepft;


import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
    
	private AndriosData mData; //Read in from saved file, passed to all future intents.
	
	
	/** Called when the activity is first created. */
   
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        
        readData();
        setConnections();
    }

	private void readData() {
		mData = new AndriosData();
		
	}

	private void setConnections() {
		TabHost mTabHost = getTabHost();
        Intent intent;
        Resources res = getResources(); 
        
        //Setup for Home Tab (Tab 0)
        intent = new Intent().setClass(this, PFTActivity.class);
        intent.putExtra("data", mData);
        mTabHost.addTab(mTabHost.newTabSpec("PFT").setIndicator("",res.getDrawable(R.drawable.tab_pft))
        		.setContent(intent));
        
        //Setup for Workout Tab (Tab 1)
        intent = new Intent().setClass(this, CFTActivity.class);
        intent.putExtra("data", mData);
        mTabHost.addTab(mTabHost.newTabSpec("CFT").setIndicator("",res.getDrawable(R.drawable.tab_cft))
        		.setContent(intent));
        
        //Setup for Profile Tab (Tab 2)
        intent = new Intent().setClass(this, BCAActivity.class);
        intent.putExtra("data", mData);
        mTabHost.addTab(mTabHost.newTabSpec("BCA").setIndicator("",res.getDrawable(R.drawable.tab_bca))
        		.setContent(intent));
         
        //Setup for Exercise Tab (Tab 3)
        intent = new Intent().setClass(this, InstructionsActivity.class);
        intent.putExtra("data", mData);
        mTabHost.addTab(mTabHost.newTabSpec("Instructions").setIndicator("",res.getDrawable(R.drawable.tab_inst))
        		.setContent(intent));
       

        //Set Tab host to Home Tab
        mTabHost.setCurrentTab(0);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}
}