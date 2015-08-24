package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONObject;

public  class Comment {
	/**
	 *     "id": 1,
	 "works": 1244,
	 "reviewer": 118,
	 "replyTarget": 0,
	 "description": "test",
	 "createTime": 1438935449000
	 */
	public long id = -1;
	public long reviewer = -1;
	public long works = -1;
	public long replyTarget = -1;
	public transient String reviewerName = "";
	public transient String targetName = "";
	public transient int avatar = -1;

	public String description = "";

	public static Comment fromJSON(JSONObject jsonObject){
		Gson gson = new Gson();
		return gson.fromJson(jsonObject.toString(), Comment.class);
	}
}
