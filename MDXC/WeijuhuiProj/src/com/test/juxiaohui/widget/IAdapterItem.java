/**
 * 
 */
package com.test.juxiaohui.widget;

import android.view.View;

/**
 * @author yh
 *
 */
public interface IAdapterItem<T> {
	
	public View getView(T data, View convertView);


}
