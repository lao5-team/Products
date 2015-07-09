package com.pineapple.mobilecraft.tumcca.data;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class MallProvinceBean {

	@SerializedName("id")
	private int id;
	@SerializedName("name")
	private String name;
	@SerializedName("parentId")
	private int parentId;
	@SerializedName("city")
	private ArrayList<MallCityBean> city;
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
	public ArrayList<MallCityBean> getCity() {
		return city;
	}
	public void setCity(ArrayList<MallCityBean> city) {
		this.city = city;
	}
	
}
