package com.test.juxiaohui.cache;


public interface DataCallback extends CustomCallback{

	public void onReady(String key, byte[] data);
}
