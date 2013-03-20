package com.riverdevs.testbluetooth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.riverdevs.testbluetooth.R;
import com.riverdevs.testbluetooth.communication.ServerThread;

public class MainActivity extends BluetoothSupportedActivity {
	
	private Handler UIHandler;
	
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
        
        final Intent i = new Intent(this, SearchDevicesActivity.class);

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
        
        UIHandler = new MainActivityHandler(getApplicationContext(), listenForConnectionButton);
    }

    private void listenForConnection(){
		new ServerThread(UIHandler).start();
		listenForConnectionButton.setEnabled(false);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
    
	private static class MainActivityHandler extends Handler{
		private final Context appContext;
		private final Button listenForConnectionButton;
		
		public MainActivityHandler(Context appContext, Button listenForConnectionButton) {
			this.appContext = appContext;
			this.listenForConnectionButton = listenForConnectionButton;
		}
		
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			Toast.makeText(appContext, bundle.getString("message"), Toast.LENGTH_SHORT).show();
			if(bundle.getString("message").equals("END") ||
					bundle.getString("message").equals("END_OK")){
				listenForConnectionButton.setEnabled(true);
			}
		}
	}
}
