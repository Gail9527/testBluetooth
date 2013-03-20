package com.riverdevs.testbluetooth.activities;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothSupportedActivity extends Activity {

	protected static final int REQUEST_BT_ENABLE = 666;
	protected static final int REQUEST_BT_DISCOVERABLE = 667;
	protected static final int ACTIVITY_CREATE = 668;
	protected static final int REQUEST_INIT_BT_FOR_NEW_DEVICE = 669;
	protected static final int REQUEST_INIT_BT_FOR_PAIRED_DEVICES = 670;
	protected static final int REQUEST_INIT_BT_FOR_LISTEN_CONNECTION = 671;
	
	protected BluetoothAdapter mBluetoothAdapter;
	
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
    
    protected void showMessage(String message){
	    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
