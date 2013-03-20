package com.riverdevs.testbluetooth.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Utility {
	
	public static void showMessageOnUI(Handler UIHandler, String message){
		if(UIHandler != null){
	    	Bundle bundle = new Bundle();
	    	bundle.putString("message", message);
	    	Message mess = new Message();
	    	mess.setData(bundle);
	    	UIHandler.sendMessage(mess);
		}
	}
}
