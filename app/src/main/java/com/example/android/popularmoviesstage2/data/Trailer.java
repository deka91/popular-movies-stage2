package com.example.android.popularmoviesstage2.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deniz Kalem on 12.08.17.
 */

public class Trailer {
	private String id;
	private String key;

	public Trailer(JSONObject jsonTrailer) throws JSONException {
		this.id = jsonTrailer.getString("id");
		this.key = jsonTrailer.getString("key");
	}

	public String getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

}
