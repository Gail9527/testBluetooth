package com.riverdevs.testbluetooth;

import java.util.HashSet;
import java.util.Set;

import com.riverdevs.testbluetooth.utils.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final int REQUEST_BT_ENABLE = 666;
	protected static final int REQUEST_BT_DISCOVERABLE = 667;
	protected static final int ACTIVITY_CREATE = 668;
	protected static final int REQUEST_INIT_BT_FOR_NEW_DEVICE = 669;
	protected static final int REQUEST_INIT_BT_FOR_PAIRED_DEVICES = 670;
	protected static final int REQUEST_INIT_BT_FOR_LISTEN_CONNECTION = 671;
	
	protected BluetoothAdapter mBluetoothAdapter;
	
	private Button listenForConnectionButton;
			
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button enableBT = (Button) findViewById(R.id.enableBTButton);
        enableBT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initBluetooth(MainActivity.this, REQUEST_BT_ENABLE);
			}
		});
        
        Button getPairedButton = (Button) findViewById(R.id.pairedButton);
        getPairedButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAlreadyPairedDevices();
			}
		});
        
        Button enableDiscoveryButton = (Button) findViewById(R.id.enableDiscoveryButton);
        enableDiscoveryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				enableDiscoverability();
			}
		});
        
        final Intent i = new Intent(this, LookForNewDeviceActivity.class);

        Button lookForNewDevicesButton = (Button) findViewById(R.id.newDevicesButton);
        lookForNewDevicesButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		        startActivityForResult(i, ACTIVITY_CREATE);	
			}
		});
        
        listenForConnectionButton = (Button) findViewById(R.id.listenForConnectionButton);
        listenForConnectionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(initBluetooth(MainActivity.this, REQUEST_INIT_BT_FOR_LISTEN_CONNECTION)){
					listenForConnection();
				}
			}
        });
        
        Utility.UIHandler = new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			Bundle bundle = msg.getData();
    			showMessage(bundle.getString("message"));
    			if(bundle.getString("message").equals("END") ||
    					bundle.getString("message").equals("END_OK")){
    				listenForConnectionButton.setEnabled(true);
    			}
    		}
    	};
    }

    private void listenForConnection(){
		new ServerThread().start();
		listenForConnectionButton.setEnabled(false);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public boolean initBluetooth(Activity caller, int requestCode){
    	
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if (mBluetoothAdapter == null) {
    		AlertDialog.Builder altDialog= new AlertDialog.Builder(this);
    		altDialog.setMessage("No bluetooth available");
    		altDialog.setNeutralButton("OK", null);
    		altDialog.show();
    		return false;
    	}
    	
    	if(! mBluetoothAdapter.isEnabled()){
    		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		caller.startActivityForResult(enableBtIntent, requestCode);
    		return false;
    	}
    	
    	return true;
    }

	public void enableDiscoverability() {
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(discoverableIntent, REQUEST_BT_DISCOVERABLE);
	}
    
    public Set<BluetoothDevice> getAlreadyPairedDevices(){
    	Set<BluetoothDevice> emptyList = new HashSet<BluetoothDevice>();
    	
    	if(mBluetoothAdapter==null || !mBluetoothAdapter.isEnabled()){
    		initBluetooth(this, REQUEST_INIT_BT_FOR_PAIRED_DEVICES);
    	}
    	else{
	    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
	    	// If there are paired devices
	    	if (pairedDevices.size() > 0) {
	    	    // Loop through paired devices
	    		StringBuffer devices = new StringBuffer();
	    	    for (BluetoothDevice device : pairedDevices) {
	    	        // Add the name and address to an array adapter to show in a ListView
	    	        devices.append(device.getName() + "\n" + device.getAddress());
	    	    }
	    	    showMessage("Paired devices found:" + devices.toString());
	    	    
		    	return pairedDevices;
	    	}
    	}
    	
    	return emptyList;
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	switch (requestCode) {
			case REQUEST_BT_ENABLE:
				if(resultCode==RESULT_CANCELED){
					showMessage("Bluetooth not activated. Please try again");					
				}
				if(resultCode==RESULT_OK){
					showMessage("Ok, bluetooth activated");					
				}
				break;
			case REQUEST_BT_DISCOVERABLE:
				if(resultCode==RESULT_CANCELED){
					showMessage("The device discoverability " +
		    				"couldn't be enabled. Please try again");					
				}
				if(resultCode==RESULT_OK){
		    		showMessage("The device discoverability " +
		    				"was enabled. Thanks");					
				}
				break;
			case REQUEST_INIT_BT_FOR_LISTEN_CONNECTION:
				if(resultCode==RESULT_CANCELED){
					showMessage("Bluetooth not activated. Please try again");					
				}
				if(resultCode==RESULT_OK){
					listenForConnection();			
				}
				break;
			default:
				break;
		}

    }
    
    protected void showMessage(String message){
	    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

	public Button getListenForConnectionButton() {
		return listenForConnectionButton;
	}
    
}
