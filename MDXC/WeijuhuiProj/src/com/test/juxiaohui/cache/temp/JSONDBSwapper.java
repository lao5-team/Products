package com.test.juxiaohui.cache.temp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class JSONDBSwapper extends SQLiteOpenHelper implements ISwapper<String, JSONObject> {

	private String mTableName;
	private static final int TRANSACTION_LIMIT = 10;
	private final String TABLE_CREATE = "CREATE TABLE "
			+ mTableName + " ("
			+ "value" +" TEXT, "
			+ "id" + " TEXT PRIMARY KEY);";
	
	public JSONDBSwapper(Context context, String name) {
		super(context, name + ".db" , null, 1);

	}
	
	public void setName(String name)
	{
		if(null == name)
		{
			throw new IllegalArgumentException("name is null!");
		}
		mTableName = name;		
	}

	@Override
	public boolean contains(String key) {
		JSONObject value = get(key);
		if(null != value)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}

	@Override
	public void put(String key, JSONObject value) {
		if(null == key)
		{
			throw new IllegalArgumentException("key is null!");
		}
		if(null == value)
		{
			throw new IllegalArgumentException("value is null!");
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", key);
		values.put("value", value.toString());
		if(db.isOpen()){
			db.insertWithOnConflict(mTableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		}
		db.close();
	}

	@Override
	public JSONObject get(String key) {
		if(null == key)
		{
			throw new IllegalArgumentException("key is null!");
		}
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(mTableName, new String[]{"value"}, "id=?", new String[]{key}, null, null, null);
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

	@Override
	public List<JSONObject> getList(List<String> keyList) {
		if(null == keyList)
		{
			throw new IllegalArgumentException("keyList is null!");
		}
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(mTableName, new String[]{"value"}, "id=?", (String[]) keyList.toArray(), null, null, null);
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		while(cursor.moveToNext())
		{
			String value = cursor.getString(cursor.getColumnIndex("id"));
			try {
				JSONObject item = new JSONObject(value);
				result.add(item);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		db.close();
		cursor.close();
		return result;
	}

	@Override
	public void putList(List<String> keyList, List<JSONObject> valueList) {
		if(keyList!=null&&valueList!=null)
		{
			if(keyList.size() == valueList.size())
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
							db.insertWithOnConflict(mTableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
						}
					}
					db.setTransactionSuccessful();
					db.endTransaction();
					db.close();
				}

			}
		}		
	}

	@Override
	public int getSize() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(mTableName, new String[]{"value"}, null, null, null, null, null);
		int result = cursor.getCount();
		db.close();
		return result;
	}

	@Override
	public void clear() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(mTableName, null, null);
		db.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(TABLE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
