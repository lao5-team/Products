package com.pineapple.mobilecraft.cache.temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.pineapple.mobilecraft.cache.temp.IListCache;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JSONCache extends SQLiteOpenHelper implements IListCache<String, JSONObject> {
	//public final static String dataPattern = "yyyy年MM月dd日hh时";
	private static final int TRANSACTION_LIMIT = 10;
	String mName;
	HashMap<String, JSONObject> mMap = new HashMap<String, JSONObject>();
	String mCreateTableString;
	
	public JSONCache(Context context, String name)
	{
		super(context, name + ".db" , null, 1);
		mName = name;
		
		mCreateTableString = "CREATE TABLE "
		+ mName + " ("
		+ "value" +" TEXT, "
		+ "id" + " TEXT PRIMARY KEY);";
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<JSONObject> valueList = new ArrayList<JSONObject>();
		if(null!=this.getWritableDatabase())
		{
			loadAll();
		}
		
	}

	//这个方法不会被外部类用到，也不要在内部调用它
	private JSONCache()
	{
		super(null, null , null, 1);
	}

//	@Override
//	public JSONObject get(String key) {
//		if(null == key)
//		{
//			throw new IllegalArgumentException("key is null!");
//		}		
//		JSONObject value = null;
//		if(mMap.containsKey(key))
//		{
//			value= mMap.get(key);
//		}
//		else
//		{
//			value = getByDB(key);
//			if(value != null)
//			{
//				mMap.put(key, value);
//			}
//		}
//		return value;
//	}



	
	@Override
	public void remove(String key) {
		if(null == key)
		{
			throw new IllegalArgumentException("key is null");
		}
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(mName, "id=?", new String[]{key});
		db.close();
		mMap.remove(key);
	}

	@Override
	public void removeList(Set<String> keys) {

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(mCreateTableString);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	private void putByDB(String key, JSONObject value)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("id", key);
		values.put("value", value.toString());
		if(db.isOpen()){
			db.insertWithOnConflict(mName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		}
		db.close();
	}
	
	private JSONObject getByDB(String key)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(mName, new String[]{"value"}, "id=?", new String[]{key}, null, null, null);
		String value = null;
		JSONObject result = null;
		while(cursor.moveToNext())
		{
			value = cursor.getString(cursor.getColumnIndex("id"));
		}
		if(null != value)
		{
			try {
				result = new JSONObject(value);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cursor.close();
		db.close();
		return result;
	}
	
	
	/** 向数据库写一个数组
	 * @param keyList
	 * @param valueList
	 */
	private void putListByDB(List<String> keyList, List<JSONObject> valueList)
	{
		if(keyList.size()<TRANSACTION_LIMIT)
		{
			for(int i=0; i<keyList.size(); i++)
			{
				put(keyList.get(i), valueList.get(i));
			}					
		}
		else
		{
			SQLiteDatabase db = this.getWritableDatabase();
			db.beginTransaction();
			for(int i=0; i<keyList.size(); i++)
			{
				ContentValues values = new ContentValues();
				values.put("id", keyList.get(i));
				values.put("value", valueList.get(i).toString());
				if(db.isOpen()){
					db.insertWithOnConflict(mName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				}
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		}
	}
	
	
	/**从数据库中读取一个数据
	 * @param keyList
	 * @return
	 */
	private List<JSONObject> getListByDB(List<String> keyList) {
		if(null == keyList)
		{
			throw new IllegalArgumentException("keyList is null!");
		}
		SQLiteDatabase db = this.getWritableDatabase();
		String []keys = new String[keyList.size()];
		Cursor cursor = db.query(mName, new String[]{"value"}, "id=?", keyList.toArray(keys), null, null, null);
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		if(cursor.getCount()>0){
			while(cursor.moveToNext())
			{
				String value = cursor.getString(cursor.getColumnIndex("value"));
				try {
					JSONObject item = new JSONObject(value);
					result.add(item);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		db.close();
		cursor.close();
		return result;
	}

	/**
	 * 从数据库载入所有数据
	 */
	private void loadAll()
	{
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<JSONObject> valueList = new ArrayList<JSONObject>();

		SQLiteDatabase db = this.getWritableDatabase();	
		Cursor cursor = db.query(mName, new String[]{"id, value"}, null, null, null, null, null);		
		while(cursor.moveToNext())
		{
			String key = cursor.getString(cursor.getColumnIndex("id"));
			String value = cursor.getString(cursor.getColumnIndex("value"));
			try {
				JSONObject item = new JSONObject(value);
				keyList.add(key);
				valueList.add(item);
				mMap.put(key, item);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		db.close();
		cursor.close();
	}

	/**
	 *
	 * @return 返回一个JSONObject数组
	 */
	public List<JSONObject> getAllItems()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		List<JSONObject> valueList = new ArrayList<JSONObject>();
		Cursor cursor = db.query(mName, null, null, null, null, null, null);
		String value = null;

		while(cursor.moveToNext())
		{
			value = cursor.getString(cursor.getColumnIndex("value"));
			if(null != value)
			{
				try {
					valueList.add(new JSONObject(value));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return valueList;
	}

	@Override
	public Set<String> getAllKeys() {
		return null;
	}

	@Override
	public boolean hasKey(String key) {
		return false;
	}

	@Override
	public List<String> getKeysBeforeItem(String key, int count) {
		SQLiteDatabase db = this.getWritableDatabase();
		List<String> keyList = new ArrayList<String>();
		if(null != key)
		{
			Cursor cursor = db.query(mName, new String[]{"id"}, "id<?", new String[]{key}, null, null, "id DESC");
			String value = null;
			
			while(cursor.moveToNext() && count >0)
			{
				value = cursor.getString(cursor.getColumnIndex("id"));
				count--;
				if(null != value)
				{
					keyList.add(value);
				}
			}

			cursor.close();			
		}
		else
		{
			Cursor cursor = db.query(mName, null, null, null, null, null, "id DESC");
			String value = null;
			
			while(cursor.moveToNext() && count >0)
			{
				value = cursor.getString(cursor.getColumnIndex("id"));
				count--;
				if(null != value)
				{
					keyList.add(value);
				}
			}
		}

		db.close();
		return keyList;
	}

	@Override
	public List<String> getKeysAfterItem(String key, int count) {
		SQLiteDatabase db = this.getWritableDatabase();
		List<String> keyList = new ArrayList<String>();
		if(null != key)
		{		
			Cursor cursor = db.query(mName, new String[]{"id"}, "id>?", new String[]{key}, null, null, "id ASC");
			String value = null;
			
			while(cursor.moveToNext() && count >0)
			{
				value = cursor.getString(cursor.getColumnIndex("id"));
				count--;
				if(null != value)
				{
					keyList.add(value);
				}
			}
			cursor.close();
		}
		else
		{
			Cursor cursor = db.query(mName, null, null, null, null, null, "id ASC");
			String value = null;
			
			while(cursor.moveToNext() && count >0)
			{
				value = cursor.getString(cursor.getColumnIndex("id"));
				count--;
				if(null != value)
				{
					keyList.add(value);
				}
			}
			cursor.close();			
		}
		db.close();
		return keyList;
	}

	@Override
	public List<JSONObject> getItems(Set<String> keyList) {
		return null;
	}

	@Override
	public void putItems(Set<String> keyList, List<JSONObject> valueList) {

	}

	/**
	 * 获取一组key对应的value
	 * @param keyList key列表
	 * @return
	 */
	public List<JSONObject> getItems(List<String> keyList) {
		if(null == keyList)
		{
			throw new IllegalArgumentException("keyList is null!");
		}	
		
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		ArrayList<String> keyDBList = new ArrayList<String>();
		for(String key:keyList)
		{
			if(mMap.containsKey(key))
			{
				JSONObject value = mMap.get(key);
				result.add(value);
			}
			/*
			 *当请求的数据量超过一定程度时，可能会出现内存数据少于数据库数据的情况
			 * */
			else 
			{
				keyDBList.add(key);
			}
		}
		List<JSONObject> valueDBList = getListByDB(keyDBList);
		try
		{
			for(int i=0; i<keyDBList.size(); i++)
			{
				mMap.put(keyDBList.get(i), valueDBList.get(i));
			}
		}
		catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		result.addAll(valueDBList);
		return result;
	}

	public void putItems(List<String> keyList, List<JSONObject> valueList) {
		if(null == keyList)
		{
			throw new IllegalArgumentException("keyList is null!");
		}	
		
		if(null == valueList)
		{
			throw new IllegalArgumentException("valueList is null!");
		}	
		
		if(keyList.size() != valueList.size())
		{
			throw new IllegalArgumentException("valueList is null!");
		}
		
		for(int i=0; i<keyList.size(); i++)
		{
			mMap.put(keyList.get(i), valueList.get(i));
		}	
		putListByDB(keyList, valueList);
	}

	/**
	 * 通过id获取一个item
	 * @param id
	 * @return 如果数据存在返回一个JSONObject否则返回null
	 */
	@Override
	public JSONObject getItem(String id)
	{
		List<String> listIds = new ArrayList<String>();
		listIds.add(id);
		List<JSONObject> listObject = getItems(listIds);
		try
		{
			return listObject.get(0);
		}
		catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void putItem(String id, JSONObject object)
	{
		List<String> listIds = new ArrayList<String>();
		listIds.add(id);

		List<JSONObject> listJSON = new ArrayList<JSONObject>();
		listJSON.add(object);
		putItems(listIds, listJSON);
	}
	
	public int getSize()
	{
		return mMap.size();
	}

	/**
	 * 清除缓存
	 */
	public void clear()
	{
		mMap.clear();
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(mName, null, null);
		db.close();
	}

	private void put(String key, JSONObject value) {
		if(null == key)
		{
			throw new IllegalArgumentException("key is null!");
		}	
		
		if(null == value)
		{
			throw new IllegalArgumentException("value is null!");
		}	
		
		mMap.put(key, value);
		putByDB(key, value);
	}
}
