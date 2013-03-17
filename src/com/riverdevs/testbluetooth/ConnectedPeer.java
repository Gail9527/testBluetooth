package com.riverdevs.testbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import com.riverdevs.testbluetooth.utils.Utility;

/**
 * Common Thread for both server/client.
 * Implements the basic communication logic once connection established
 * 
 * @author charro
 *
 */
public class ConnectedPeer {
	
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
 
    public ConnectedPeer(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        	Utility.showMessageOnUI("Error when getting input/output stream from socket");
        }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        
    }
    
    public void startListening(){
        // Keep listening to the InputStream until an exception occurs or END message received
        while (true) {
            try {
            	ObjectInputStream ois = new ObjectInputStream(mmInStream);

				try {
					String messageString = (String) ois.readObject();
	            	// Send message to the UI to update it
					Utility.showMessageOnUI(messageString);
	            	
	            	// If response to ending message, close communication without sending response
        			if(messageString.equals("END_OK")){
        				break;
        			}
        			
        			sendResponseTo(messageString);
        			
        			// If ending message received, finish thread after sending response
        			if(messageString.equals("END")){
        	            break;
        			}
	            	
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
            } catch (IOException e) {
            	e.printStackTrace();
            	Utility.showMessageOnUI("Error when trying to receive message from peer");
                break;
            }
        }
        
        cancel();
    }
 
    private void sendResponseTo(String messageString) {
		String[] requestMessages = {"INIT", "REQ1", "REQ2", "END"};
		String[] responseMessages = {"INIT_OK", "REQ1_OK", "REQ2_OK", "END_OK"};

		//Wait 2 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=0; i<requestMessages.length; i++){
			// Received a request message, send response
			if(requestMessages[i].equals(messageString)){
				Bundle mess = new Bundle();
				mess.putString("message", responseMessages[i]);
				sendMessageToRemote(mess);
			}
			// Received a response to a message, send next request
			else if(responseMessages[i].equals(messageString)){
				Bundle mess = new Bundle();
				mess.putString("message", requestMessages[i+1]);
				sendMessageToRemote(mess);				
			}
		}
	}

	/* Call this from the main activity to send data to the remote device */
    public void sendMessageToRemote(Bundle message){
    	ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(mmOutStream);
	    	oos.writeObject(message.getString("message"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

}
