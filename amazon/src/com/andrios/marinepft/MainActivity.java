package com.andrios.marinepft;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import net.robotmedia.billing.helper.AbstractBillingActivity;
import net.robotmedia.billing.model.Transaction;
import net.robotmedia.billing.model.Transaction.PurchaseState;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class MainActivity extends AbstractBillingActivity implements Serializable, Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3088858143996312467L;
	protected static final int PROFILEVIEW = 1;
	Button profileBTN, logBTN, calcBTN, aboutBTN, instructionBTN;
	boolean premium;
	AndriosData mData;
	Profile profile;
	GoogleAnalyticsTracker tracker;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainactivity);
        

        AppRater.app_launched(this);
        setConnections();
        setOnClickListeners();
        restoreTransactions();
        readData();
        mData = new AndriosData();
    	updateOwnedItems();
    	testProfile();
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



	private void testProfile() {
		if(profile.getName().equals("Click to Set Name")){
			createProfileDialog();
		}
		
	}
	
	private void createProfileDialog(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Please create a profile");
		alert.setMessage("Your profile will allow you to set defaults for the calculators and track upcoming PFTs & CFTs");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(MainActivity.this.getBaseContext(), ProfileActivity.class);
				intent.putExtra("profile", profile);
				startActivityForResult(intent, PROFILEVIEW);
				
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



	private void setConnections() {
		profileBTN = (Button) findViewById(R.id.mainActivityProfileBTN);
		logBTN = (Button) findViewById(R.id.mainActivityLogBTN);
		calcBTN = (Button) findViewById(R.id.mainActivityCalculatorsBTN);
		aboutBTN = (Button) findViewById(R.id.mainActivityAboutBTN);
		instructionBTN = (Button) findViewById(R.id.mainActivityInstructionsBTN);
		
	}



	private void setOnClickListeners() {
		profileBTN.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), ProfileActivity.class);
				intent.putExtra("profile", profile);
				startActivityForResult(intent, PROFILEVIEW);
			}
			
		});
		logBTN.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				if(premium){
					Intent intent = new Intent(v.getContext(), LogActivity.class);
					mData.setAge(profile.getAge());
					mData.setGender(profile.isMale());
					System.out.println("Main Age" + mData.getAge());
					intent.putExtra("data", mData);
					startActivity(intent);
				}else{
					setAlertDialog();
				}
			
				
			}
			
		});
		calcBTN.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), CalculatorTabsActivity.class);
				intent.putExtra("premium", premium);
				mData.setAge(profile.getAge());
				mData.setGender(profile.isMale());

				intent.putExtra("data", mData);
				startActivity(intent);
				
			}
			
		});
		aboutBTN.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), AboutActivity.class);
				
				startActivity(intent);
				
			}
			
		});
		instructionBTN.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), InstructionsActivity.class);
				intent.putExtra("premium", premium);
				startActivity(intent);
				
			}
			
		});
		
	}



	public byte[] getObfuscationSalt() {
		return new byte[] {41, -90, -116, -41, 66, -53, 122, -110, -127, -96, -88, 77, 127, 115, 1, 73, 57, 110, 48, -116};
		}



	public String getPublicKey() {
		
		return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsWpPBgnPmwEHkkfcy11L5/gdEzeVGy/xxFA719PrQeX8a1VlJAQeWjvxVd5xgpcmcUXnt4t+0IWAfL1plRIH6OUCYWbZlhJv/QdqmC5v7WAH8U925Yl6o0GNxdgZDfVyBDw/wboSbQ1dxbEMXZ0Jqpfz2DApszhxC9vj9xLIo6hBm1dGYWkTVPn7LLiRfnCkxJgRNxtMrEP8FwnebV3Lvk8520/0VQj8wVU3QZaGPEQ3yO+z664D3Zlx9p0hMk54xeoJo86OwvXw1lKA8vlXBCc1eHhHa6QhAUn0SNVq9e1sF3rsxKJWbT1tUvK9qHHinrMjXF5095jx+evH58pn7wIDAQAB";
	}



	@Override
	public void onBillingChecked(boolean supported) {
		
		System.out.println("Billing Supported: " + supported);//TODO REmove
	}



	@Override
	public void onPurchaseStateChanged(String itemId, PurchaseState state) {
		if(itemId.equals("premium_features") && PurchaseState.PURCHASED.equals(state)){
			premium = true;
			System.out.println("Premium Features: Purchased");//TODO REmove
		}else if(itemId.equals("premium_features") && PurchaseState.CANCELLED.equals(state)){
			System.out.println("Premium Features: Cancelled ");//TODO REmove
		}if(itemId.equals("premium_features") && PurchaseState.REFUNDED.equals(state)){
			System.out.println("Premium Features: Refunded ");//TODO REmove
		}
	}



	@Override
	public void onRequestPurchaseResponse(String itemId, ResponseCode response) {
		
	}
	
	private void updateOwnedItems() {
		System.out.println("Update Owned Items");//TODO REmove
		List<Transaction> transactions = BillingController.getTransactions(this);
		final ArrayList<String> ownedItems = new ArrayList<String>();
		premium = false;
		System.out.println("Premium set to false for now");//TODO REmove
		for (Transaction t : transactions) {
			if (t.purchaseState == PurchaseState.PURCHASED) {
				if(t.productId.equals("premium_features")){
					premium = true;
					System.out.println("Premium Verified");//TODO REmove
					break;
				}
				
				
			}
		}
		

		
	}
	private void setAlertDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.alert_dialog_premium_features, null);
		
		builder.setView(layout)
				.setTitle("Premium Features")
				.setPositiveButton("Buy Now!", new DialogInterface.OnClickListener(){
					
					
					public void onClick(DialogInterface dialog, int which) {	
						requestPurchase("premium_features");
					}
				})
				.setNegativeButton("No Thanks", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						
						
					}
					
				});
		AlertDialog ad = builder.create();
		ad.show();
	}
	
	private void readData() {
		
		try {
			FileInputStream fis = openFileInput("profile");
			ObjectInputStream ois = new ObjectInputStream(fis);

			profile = (Profile) ois.readObject();
			profile.addObserver(MainActivity.this);
			ois.close();
			fis.close();
			
		} catch (Exception e) {
			profile = new Profile();
			
		
	}
	}



	public void update(Observable observable, Object data) {
		System.out.println("PROFILE UPDATED");
		
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
    	if (requestCode == PROFILEVIEW) {
    		if (resultCode == RESULT_OK) {
    			profile = (Profile) intent.getSerializableExtra("profile");
    			//setConnections();
    		} else {
    			
    			Toast.makeText(this, "Changes  Canceled", Toast.LENGTH_SHORT).show();
    		}
    	}
    }
}
