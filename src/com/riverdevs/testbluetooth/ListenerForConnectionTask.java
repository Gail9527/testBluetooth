package com.riverdevs.testbluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.riverdevs.testbluetooth.utils.Constants;

public class ListenerForConnectionTask{ // extends AsyncTask<Void, Void, BluetoothSocket>{
//	private BluetoothServerSocket mmServerSocket;
//	private BluetoothAdapter adapter;
//	private MainActivity UIActivity;
//	
//	public ListenerForConnectionTask(MainActivity UIActivity) {
//		this.UIActivity = UIActivity;
//		adapter = BluetoothAdapter.getDefaultAdapter();
//	}
//	
//    /** The system calls this to perform work in a worker thread and
//      * delivers it the parameters given to AsyncTask.execute() */
//    protected BluetoothSocket doInBackground(Void... params) {
//        BluetoothServerSocket tmp = null;
//        try {
//            // MY_UUID is the app's UUID string, also used by the client code
//            tmp = adapter.listenUsingRfcommWithServiceRecord(Constants.APP_NAME, Constants.APP_UUID);
//        } catch (IOException e) { }
//        
//        mmServerSocket = tmp;
//        
//        BluetoothSocket socket = null;
//        // Keep listening until exception occurs or a socket is returned
//            try {
//                socket = mmServerSocket.accept();
//            } catch (IOException e) {
//            	UIActivity.showMessage("Error when trying to stablish a connection");
//            }
//        
//        if(socket==null){
//        	return null;
//        }
//        else{
//        	return socket;
//        }
//    }
//    
//    /** The system calls this to perform work in the UI thread and delivers
//      * the result from doInBackground() */
//    protected void onPostExecute(BluetoothSocket socket) {
//        if(socket == null){
//        	UIActivity.showMessage("Not connected with anybody");
//        	UIActivity.getListenForConnectionButton().setEnabled(true);
//        }
//        else{
//        	UIActivity.showMessage("OLE !! Connected with " + socket.getRemoteDevice().getName());
//        	
//        	Handler handler = new Handler(){
//        		@Override
//        		public void handleMessage(Message msg) {
//        			Bundle bundle = msg.getData();
//        			UIActivity.showMessage(bundle.getString("message"));
//        			if(bundle.getString("message").equals("END")){
//        				UIActivity.getListenForConnectionButton().setEnabled(true);
//        			}
//        		}
//        	};
//        	UIActivity.setHandler(handler);
//        	
//        	// Start to listen for messages
//        	ConnectedThread connectedThread = new ConnectedThread(socket, handler);
//        	connectedThread.start();
//        }
//
//    }

}
