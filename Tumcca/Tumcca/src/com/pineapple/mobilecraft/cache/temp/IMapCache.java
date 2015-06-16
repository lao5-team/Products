package com.pineapple.mobilecraft.cache.temp;

import java.util.List;

/**当get方法被调用时，cache会在自己的集合中查找，如果找不到，就会去Swaper中查找，
 * 如果也找不到，则调用RemoteLoader下载。然后调用put，放到自身的集合中。
 * 
 * 调用put时，如果有数据需要被换出，则调用Swaper的put，将数据换出
 * @author yh
 *
 * @param <K>
 * @param <V>
 */
public interface IMapCache<K, V> {
	void setSwaper(ISwapper<K, V> swapper);
	//void setRemoteLoader(IRemoteLoader<K, V> loader);
	V get(K key);
	void put(K key, V value);
	List<V> getList(List<K> keyList);
	void putList(List<K> keyList, List<V> valueList);
	void remove(K key);
	

}
