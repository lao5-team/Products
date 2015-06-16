package com.pineapple.mobilecraft.cache.temp;

import java.util.List;

/** 用于数据的远程加载。
 * @author yh
 *
 * @param <K>
 * @param <V>
 */
public interface IRemoteLoader<K,V> {
	
	V loadData(K key);
	
	List<V> loadDataList(List<K> keyList);
}
