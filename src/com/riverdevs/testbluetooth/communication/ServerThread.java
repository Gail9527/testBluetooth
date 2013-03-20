package com.riverdevs.testbluetooth.communication;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.riverdevs.testbluetooth.utils.Constants;
import com.riverdevs.testbluetooth.utils.Utility;

public class ServerThread extends Thread{
	private BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter adapter;
	private Handler UIHandler;
	
	public ServerThread(Handler UIHandler) {
		this.UIHandler = UIHandler;
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
        	Utility.showMessageOnUI(UIHandler, "Not connected with anybody");
        }
        
        mmServerSocket = tmp;
        
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
            try {
                socket = mmServerSocket.accept();
                
                if(socket == null){
                	Utility.showMessageOnUI(UIHandler, "Not connected with anybody");
                }
                else{
                	Utility.showMessageOnUI(UIHandler, "OLE !! Connected with " + socket.getRemoteDevice().getName());
                	
                	// Start to listen for messages
                	ConnectedPeer serverPeer = new ConnectedPeer(UIHandler, socket);
                	serverPeer.startListening();
                }
                
                // Once created the communication socket, close the server socket
                mmServerSocket.close();
            } catch (IOException e) {
            	Utility.showMessageOnUI(UIHandler, "Error when trying to stablish a connection");
            }
        
	}

}

