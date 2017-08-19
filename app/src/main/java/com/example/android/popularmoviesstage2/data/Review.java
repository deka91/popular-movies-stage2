package com.example.android.popularmoviesstage2.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deniz Kalem on 08.08.17.
 */

public class Review {
	private String id;
	private String author;
	private String content;

	public String getAuthor() {
		return author;
	}

	public String getId() {
		return id;
	}

	public String getContent() {
		return content;
	}


	public Review(JSONObject review) throws JSONException {
		this.id = review.getString("id");
		this.author = review.getString("author");
		this.content = review.getString("content");
	}

}
