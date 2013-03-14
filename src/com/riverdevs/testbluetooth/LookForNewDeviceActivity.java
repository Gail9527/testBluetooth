package com.riverdevs.testbluetooth;

import java.io.IOException;
import java.util.ArrayList;

import com.riverdevs.testbluetooth.utils.Constants;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class LookForNewDeviceActivity extends MainActivity {
	
	protected ArrayList<Oponent> foundDevices = new ArrayList<Oponent>();
	
	protected BroadcastReceiver mReceiver;
	
	private Button refreshDevicesButton;
	private ProgressBar findDevicesProgressBar;
	private ListView foundDevicesListView;
	
	private ArrayAdapter<Oponent> adapter;
	
	/**
	 *  Checks if BT active and ask for enabling it first in case not active.
	 *  Once active, start looking for new devices
	 */
    public void checkBTAndStartLookingForNewDevices(){
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	
    	if(mBluetoothAdapter==null || !mBluetoothAdapter.isEnabled()){
    		initBluetooth(this, REQUEST_INIT_BT_FOR_NEW_DEVICE);
    	}
    	else{
    		lookForNewDevices();
    	}
    }
	
    /**
     * Looks for new devices and includes them in the list when found
     */
    private void lookForNewDevices(){
    	
    	if(! mBluetoothAdapter.startDiscovery()){
			showMessage("Couldn't start looking for devices");
		}
		else{
			// Create a BroadcastReceiver to manage the events of the discovery
			if(mReceiver == null){
				mReceiver = new BroadcastReceiver() {
				    public void onReceive(Context context, Intent intent) {
				        String action = intent.getAction();
				        
				        // When discovery finds a new device
				        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				            // Get the BluetoothDevice object from the Intent
				            Oponent device = 
				            		new Oponent((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
				            showMessage("Found : " + device + "\n" + device.getDevice().getAddress());
				            if(! foundDevices.contains(device)){
				            	foundDevices.add(device);
					            adapter.notifyDataSetChanged();
				            }
				        }
				        
				        // When discovery cycle finished
				        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				        	showMessage("Discovery Finished");
				        	refreshDevicesButton.setVisibility(View.VISIBLE);
				        	findDevicesProgressBar.setVisibility(View.GONE);
				        }
				    }
				};
				// Register the BroadcastReceiver
				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				registerReceiver(mReceiver, filter);
			}
			
			findDevicesProgressBar.setVisibility(View.VISIBLE);
			refreshDevicesButton.setVisibility(View.GONE);
		}
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mReceiver != null){
			unregisterReceiver(mReceiver);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("alreadyRendered", true);
		outState.putParcelableArrayList("foundDevices", foundDevices);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get previous state if any
		if(savedInstanceState != null){
			foundDevices = savedInstanceState.getParcelableArrayList("foundDevices");
		}
		
		setContentView(R.layout.activity_look_for_new_device);
		
        refreshDevicesButton = (Button) findViewById(R.id.refreshDevicesButton);
        refreshDevicesButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkBTAndStartLookingForNewDevices();
			}
		});
        
        findDevicesProgressBar = (ProgressBar) findViewById(R.id.findDevicesProgressBar);
        
        foundDevicesListView = (ListView) findViewById(R.id.foundDevicesListView);
        foundDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Oponent oponent = (Oponent) parent.getItemAtPosition(position);
        		connectWithOponent(oponent);
        	}

		});
        adapter = new ArrayAdapter<Oponent>(this, 
    	        android.R.layout.simple_list_item_1, foundDevices);
    	foundDevicesListView.setAdapter(adapter);
        
		// Show the Up button in the action bar.
//		getActionBar().setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState == null || !savedInstanceState.getBoolean("alreadyRendered")){
            checkBTAndStartLookingForNewDevices();
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_look_new_device, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
			case REQUEST_INIT_BT_FOR_NEW_DEVICE:
				if(resultCode==RESULT_CANCELED){
					showMessage("Couldn't look for devices. Please try again");					
				}
				if(resultCode==RESULT_OK){
		    		checkBTAndStartLookingForNewDevices();				
				}
			default:
				break;
		}
	}
	
	private void connectWithOponent(Oponent oponent) {
//		ClientThread client = new ClientThread(oponent.getDevice());
//		client.start();
		new ConnectWithServerTask().execute(oponent.getDevice());
	}
	
	private class ConnectWithServerTask extends AsyncTask<BluetoothDevice, Void, BluetoothDevice>{
		private BluetoothSocket mmSocket;
	    private BluetoothDevice mmDevice;
	    private BluetoothAdapter adapter;
	    
	    public ConnectWithServerTask() {
	    	adapter = BluetoothAdapter.getDefaultAdapter();
		}
	    
		@Override
		protected BluetoothDevice doInBackground(BluetoothDevice... params) {
			 BluetoothSocket tmp = null;
		     mmDevice = params[0];
		 
		        // Get a BluetoothSocket to connect with the given BluetoothDevice
		        try {
		            // MY_UUID is the app's UUID string, also used by the server code
		            tmp = mmDevice.createRfcommSocketToServiceRecord(Constants.APP_UUID);
		        } catch (IOException e) { }
		        mmSocket = tmp;
		     // Cancel discovery because it will slow down the connection
		        adapter.cancelDiscovery();
		 
		        try {
		            // Connect the device through the socket. This will block
		            // until it succeeds or throws an exception
		            mmSocket.connect();
		        } catch (IOException connectException) {
		            // Unable to connect; close the socket and get out
		            try {
		            	Log.d("Client", "Unable to connect to " + 
		            			mmDevice.getName() + " : " + connectException.getMessage());
//		            	parentActivity.showMessage("Unable to connect to " + 
//		            			mmDevice.getName() + " : " + connectException.getMessage());
		                mmSocket.close();
		                return null;
		            } catch (IOException closeException) { }
		        }
		 
		        // Do work to manage the connection (in a separate thread)
		    	Log.d("Client", "Connected with " + mmDevice.getName());
	            return mmDevice;
		}
		
        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
       protected void onPostExecute(BluetoothDevice device) {
           if(device == null){
           	showMessage("Not connected with anybody");
           }
           else{
           	showMessage("OLE !! Connected with " + device.getName());
           }
       }
		
	}
}
