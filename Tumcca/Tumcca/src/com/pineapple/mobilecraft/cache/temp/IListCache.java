package com.pineapple.mobilecraft.cache.temp;

import org.json.JSONObject;

import java.util.List;
import java.util.Set;

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
	List<V> getItems(Set<K> keyList);
	
	/**
	 * 插入一组数据
	 * @param keyList
	 * @param valueList
	 */
	void putItems(Set<K> keyList, List<V> valueList);

	void putItem(K key, V value);

	V getItem(K key);



	/**
	 * 移除一个key对应的值
	 * @param key
	 */
	void remove(K key);

	void removeList(Set<String> keys);

	public List<JSONObject> getAllItems();

	public Set<K> getAllKeys();

	boolean hasKey(K key);

}
