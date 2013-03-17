package com.riverdevs.testbluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;

import com.riverdevs.testbluetooth.utils.Constants;
import com.riverdevs.testbluetooth.utils.Utility;

public class ClientThread extends Thread {
	private BluetoothSocket mmSocket;
	private BluetoothDevice mmDevice;
	private BluetoothAdapter adapter;

	public ClientThread(BluetoothDevice device) {
		adapter = BluetoothAdapter.getDefaultAdapter();
		mmDevice = device;
		
		BluetoothSocket tmp = null;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server
			// code
			tmp = mmDevice
					.createRfcommSocketToServiceRecord(Constants.APP_UUID);
		} catch (IOException e) {
		}
		mmSocket = tmp;
	}

	@Override
	public void run() {

		try {
			setName("ClientThread");
			if (mmSocket == null) {
				
				Utility.showMessageOnUI("Not possible to connect with " + mmDevice.getName());
			} else {
				// Cancel discovery because it will slow down the connection
				adapter.cancelDiscovery();
				
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
				
				// Do work to manage the connection (in a separate thread)
				Log.d("Client", "Connected with " + mmDevice.getName());
				Utility.showMessageOnUI("Connected with " + mmDevice.getName());
				
				ConnectedPeer clientPeer = new ConnectedPeer(mmSocket);

				// Start the communication chain
				Bundle bundle = new Bundle();
				bundle.putString("message", "INIT");
				clientPeer.sendMessageToRemote(bundle);
				
				// Start listening for the response
				clientPeer.startListening();
			}
			
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {
				Utility.showMessageOnUI("Unable to connect to " + mmDevice.getName());
				mmSocket.close();
			} catch (IOException closeException) {
				
			}
		}
	}

}
