package com.test.juxiaohui.cache.temp;

import java.util.List;

public interface IListCache<K,V> {
	/**
	 * 获取某个key之前，count个key的值
	 * @param key 如果key为null，则返回数据库倒数count个key
	 * @param count 
	 * @return key的列表
	 */
	List<K> getKeysBeforeItem(K key, int count);
	
	
	
	
	/**
	 * 获取某个key之后，count个key的值
	 * @param key 如果key为null，则返回数据库正数count个key
	 * @param count
	 * @return key的列表
	 */
	List<K> getKeysAfterItem(K key, int count);
	
	/**
	 * 返回一组key对应的值
	 * @param keyList key列表
	 * @return
	 */
	List<V> getItems(List<K> keyList);
	
	/**
	 * 插入一组数据
	 * @param keyList
	 * @param valueList
	 */
	void putItems(List<K> keyList, List<V> valueList);
	
	/**
	 * 移除一个key对应的值
	 * @param key
	 */
	void remove(K key);
}
