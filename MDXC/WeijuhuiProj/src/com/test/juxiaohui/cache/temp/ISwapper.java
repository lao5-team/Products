package com.test.juxiaohui.cache.temp;

import java.util.List;

/**进行数据被从缓存被换出时，存储到新介质的操作，新介质可以是文件，数据库，甚至是云。
 * @author yh
 *
 * @param <K>
 * @param <V>
 */
public interface ISwapper<K,V> {
	
	int getSize();
	
	/** 检查是key否存在
	 * @param key
	 * @return
	 */
	boolean contains(K key);
	
	/** 插入一个数据
	 * @param key
	 * @param value
	 */
	void put(K key, V value);
	
	/** 获取某个数据
	 * @param key
	 * @return
	 */
	V get(K key);
	
	/**
	 * 获取一组数据
	 * @param keyList
	 * @return
	 */
	List<V> getList(List<K> keyList);
	
	/**插入一组数据
	 * @param keyList
	 * @param valueList
	 */
	void putList(List<K> keyList, List<V> valueList);
	
	/**
	 * 清空swap
	 */
	void clear();
	
	

}
