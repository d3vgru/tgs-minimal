package org.theglobalsquare.framework.values;

import java.io.Serializable;

import org.json.*;

import org.theglobalsquare.framework.TGSObject;

public class TGSCommunity extends TGSObject implements Serializable {
	// serializable
	private static final long serialVersionUID = -6538565948263862504L;
	
	// incoming ints need to be divided by 10^COORDINATE_SCALING_FACTOR to get degrees
	public static final int COORDINATE_SCALING_FACTOR = 6;
	
	// verbs
	public static final String JOIN = "join";
	public static final String LEAVE = "leave";
	public static final String CREATE = "create";
	public static final String LOAD = "load";
	public static final String JOINED = "joined";
	public static final String LEFT = "left";
	public static final String CREATED = "created";
	public static final String DATA = "data";

	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		if(description != null)
			this.description = description.trim();
	}
	
	private String thumbnailHash;
	
	public String getThumbnailHash() {
		return thumbnailHash;
	}

	public void setThumbnailHash(String thumbnailHash) {
		this.thumbnailHash = thumbnailHash;
	}

	private double latitude;
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLatitude(int latitudeScaled) {
		this.latitude = latitudeScaled / Math.pow(10, COORDINATE_SCALING_FACTOR);
	}
	
	private double longitude;
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLongitude(int longitudeScaled) {
		this.longitude = longitudeScaled / Math.pow(10, COORDINATE_SCALING_FACTOR);
	}
	
	private int radius; // in km, I think
	
	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	private String cid;
	
	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}
	
	private TGSMessageList messages;
	
	public TGSMessageList getMessages() {
		return messages;
	}

	public void setMessages(TGSMessageList messages) {
		this.messages = messages;
	}

	public TGSCommunity() {
		this.messages = new TGSMessageList();
	}
	
	public void updateFrom(TGSCommunity c) {
		setName(c.getName());
		setDescription(c.getDescription());
		// skip the cid (unless current cid is null?)
		setThumbnailHash(c.getThumbnailHash());
		setLatitude(c.getLatitude());
		setLongitude(c.getLongitude());
		setRadius(c.getRadius());
		// FIXME merge messages instead of assignment
		setMessages(c.getMessages());
	}
	
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject o = super.toJsonObject();
		o.put("description", getDescription());
		o.put("cid", getCid());
		o.put("thumbnailHash", getThumbnailHash());
		o.put("latitude", getLatitude());
		o.put("longitude", getLongitude());
		o.put("radius", getRadius());
		TGSMessageList m = getMessages();
		if(m != null)
			o.put("messages", m.toJsonArray());
		return o;
	}	
}
