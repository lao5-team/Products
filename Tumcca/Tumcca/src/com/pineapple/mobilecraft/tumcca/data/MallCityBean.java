package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.annotations.SerializedName;

public class MallCityBean {

	@SerializedName("id")
	private int id;
	@SerializedName("name")
	private String name;
	@SerializedName("parentId")
	private int parentId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
}
