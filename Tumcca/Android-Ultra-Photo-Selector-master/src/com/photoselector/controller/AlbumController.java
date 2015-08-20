package com.photoselector.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import com.photoselector.R;
import com.photoselector.model.AlbumModel;
import com.photoselector.model.PhotoModel;

public class AlbumController {

	private ContentResolver resolver;
	private Context context;
	List<AlbumModel> mAlbums = new ArrayList<AlbumModel>();
	public AlbumController(Context context) {
		resolver = context.getContentResolver();
		this.context = context;
		loadAlbums();

	}
	public List<PhotoModel> getCurrent(){
		return getPhotoesInAlbum("/");
	}


	/** ��ȡ�����Ƭ�б� */


	/** ��ȡ��������б� */
	public List<AlbumModel> getAlbums() {
		return mAlbums;
	}

	/** ��ȡ��Ӧ����µ���Ƭ */
	public List<PhotoModel> getPhotoesInAlbum(String name) {
		for(AlbumModel albumModel:mAlbums){
			if(albumModel.id.equals(name)){
				return albumModel.getPhotos();
			}
		}
		return new ArrayList<PhotoModel>();
	}

	private void loadAlbums(){
		List<AlbumModel> albums = new ArrayList<AlbumModel>();
		Map<String, AlbumModel> map = new HashMap<String, AlbumModel>();
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
				ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE }, null, null, null);
		if (cursor == null || !cursor.moveToNext())
		{
			mAlbums =  new ArrayList<AlbumModel>();
			return;
		}
		cursor.moveToLast();
		AlbumModel current = new AlbumModel("/", context.getString(R.string.all_photo), 0, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)), true); // "�����Ƭ"���
		albums.add(current);
		current.setPhotos(loadPhoto(current.id));
		do {
			if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < 1024 * 10)
				continue;

			current.increaseCount();
			String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
			if (map.keySet().contains(name))
				map.get(name).increaseCount();
			else {
				AlbumModel album = new AlbumModel(name, name, 1, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				album.setPhotos(loadPhoto(name));
				map.put(name, album);
				albums.add(album);

			}
		} while (cursor.moveToPrevious());
		mAlbums =  albums;
	}

	private List<PhotoModel> loadPhoto(String name){
		if(name.equals("/")){
			return loadCurrent();
		}else{
			Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
							ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE }, "bucket_display_name = ?",
					new String[] { name }, ImageColumns.DATE_ADDED);
			if (cursor == null || !cursor.moveToNext())
				return new ArrayList<PhotoModel>();
			List<PhotoModel> photos = new ArrayList<PhotoModel>();
			cursor.moveToLast();
			do {
				if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
					PhotoModel photoModel = new PhotoModel();
					photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
					photos.add(photoModel);
				}
			} while (cursor.moveToPrevious());
			return photos;
		}

	}

	private List<PhotoModel> loadCurrent() {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
				ImageColumns.DATE_ADDED, ImageColumns.SIZE }, null, null, ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<PhotoModel>();
		List<PhotoModel> photos = new ArrayList<PhotoModel>();
		cursor.moveToLast();
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
				PhotoModel photoModel = new PhotoModel();
				photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				photos.add(photoModel);
			}
		} while (cursor.moveToPrevious());
		for(AlbumModel albumModel:mAlbums){
			if(albumModel.id.equals("/")){
				albumModel.setPhotos(photos);
			}
		}
		return photos;
	}
}
