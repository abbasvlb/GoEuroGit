package com.app.goeuro;

import org.json.JSONException;
import org.json.JSONObject;

public class Result {
	
	private String TypePos;
	private int Id;
	private String Name;
	private String TypeLoc;
	private double latitude;
	private double longitude;
	private int distance;
	
	public Result(JSONObject e, float d) throws JSONException {
		TypePos = e.getString("_type");
		Id = e.getInt("_id");
		Name = e.getString("name");
		TypeLoc = e.getString("type");
		JSONObject innerObj = e.getJSONObject("geo_position");
		latitude = innerObj.getDouble("latitude");
		longitude = innerObj.getDouble("longitude");
		distance = (int) d;
	}

	public String getTypePos() {
		return TypePos;
	}

	public void setTypePos(String typePos) {
		TypePos = typePos;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getTypeLoc() {
		return TypeLoc;
	}

	public void setTypeLoc(String typeLoc) {
		TypeLoc = typeLoc;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}
