package com.riverdevs.testbluetooth;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.riverdevs.testbluetooth.utils.Constants;

public class MainActivity extends Activity {

	protected static final int REQUEST_BT_ENABLE = 666;
	protected static final int REQUEST_BT_DISCOVERABLE = 667;
	protected static final int ACTIVITY_CREATE = 668;
	protected static final int REQUEST_INIT_BT_FOR_NEW_DEVICE = 669;
	protected static final int REQUEST_INIT_BT_FOR_PAIRED_DEVICES = 670;
	
	protected BluetoothAdapter mBluetoothAdapter;
	
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
        
        Button listenForConnectionButton = (Button) findViewById(R.id.listenForConnectionButton);
        listenForConnectionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new ListenerForConnectionTask().execute();
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void initBluetooth(Activity caller, int requestCode){
    	
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if (mBluetoothAdapter == null) {
    		AlertDialog.Builder altDialog= new AlertDialog.Builder(this);
    		altDialog.setMessage("No bluetooth available");
    		altDialog.setNeutralButton("OK", null);
    		altDialog.show();
    		return;
    	}
    	
    	if(! mBluetoothAdapter.isEnabled()){
    		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		caller.startActivityForResult(enableBtIntent, requestCode);
    	}
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
			default:
				break;
		}

    }
    
    protected void showMessage(String message){
	    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    
    private class ListenerForConnectionTask extends AsyncTask<Void, Void, BluetoothDevice>{
    	private BluetoothServerSocket mmServerSocket;
    	private BluetoothAdapter adapter;
    	
    	public ListenerForConnectionTask() {
			adapter = BluetoothAdapter.getDefaultAdapter();
		}
    	
        /** The system calls this to perform work in a worker thread and
          * delivers it the parameters given to AsyncTask.execute() */
        protected BluetoothDevice doInBackground(Void... params) {
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = adapter.listenUsingRfcommWithServiceRecord(Constants.APP_NAME, Constants.APP_UUID);
            } catch (IOException e) { }
            
            mmServerSocket = tmp;
            
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                }
            
            if(socket==null){
            	return null;
            }
            else{
            	BluetoothDevice device = socket.getRemoteDevice();
            	try {
					mmServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            	return device;
            }
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
