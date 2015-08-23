package com.pineapple.mobilecraft.cache;


public interface DataCallback extends CustomCallback{

	public void onReady(String key, byte[] data);
}
