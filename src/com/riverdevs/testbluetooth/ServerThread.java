package com.riverdevs.testbluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.riverdevs.testbluetooth.utils.Constants;
import com.riverdevs.testbluetooth.utils.Utility;

public class ServerThread extends Thread{
	private BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter adapter;
	
	public ServerThread() {
		adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public void run() {
		setName("ServerThread");
		
		BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = adapter.listenUsingRfcommWithServiceRecord(Constants.APP_NAME, Constants.APP_UUID);
        } catch (IOException e) { 
        	Utility.showMessageOnUI("Not connected with anybody");
        }
        
        mmServerSocket = tmp;
        
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
            try {
                socket = mmServerSocket.accept();
                
                if(socket == null){
                	Utility.showMessageOnUI("Not connected with anybody");
//                	UIActivity.getListenForConnectionButton().setEnabled(true);
                }
                else{
                	Utility.showMessageOnUI("OLE !! Connected with " + socket.getRemoteDevice().getName());
                	
                	// Start to listen for messages
                	ConnectedPeer serverPeer = new ConnectedPeer(socket);
                	serverPeer.startListening();
                }
                
                mmServerSocket.close();
            } catch (IOException e) {
            	Utility.showMessageOnUI("Error when trying to stablish a connection");
            }
        
	}

}

