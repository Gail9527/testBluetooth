package com.riverdevs.testbluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Oponent implements Parcelable{

	private int mData;
	private BluetoothDevice device;
	
	public Oponent(BluetoothDevice device) {
		super();
		this.device = device;
	}

	public Oponent(Parcel in) {
		mData = in.readInt();
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Oponent other = (Oponent) obj;
		if (device == null) {
			if (other.device != null)
				return false;
		} else if (!device.equals(other.device))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if(device != null){
			if(! TextUtils.isEmpty(device.getName())){
				return device.getName();
			}
			else{
				return device.getAddress();
			}
		}
		else{
			return "No device";
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mData);
		out.writeParcelable(device, 0);
	}
	
	public static final Parcelable.Creator<Oponent> CREATOR
	    = new Parcelable.Creator<Oponent>() {
		public Oponent createFromParcel(Parcel in) {
		    return new Oponent(in);
		}
		
		public Oponent[] newArray(int size) {
		    return new Oponent[size];
		}
	};
}
