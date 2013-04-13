package org.theglobalsquare.framework.values;

import org.json.*;

import org.theglobalsquare.framework.TGSObject;
import org.theglobalsquare.framework.TGSObjectList;

public class TGSCommunity extends TGSObject {
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
	}
	
	private String mid;

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	private TGSObjectList messages;
	
	public TGSObjectList getMessages() {
		return messages;
	}

	public void setMessages(TGSObjectList messages) {
		this.messages = messages;
	}

	public TGSCommunity() {
		this.messages = new TGSObjectList();
	}
	
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject o = super.toJsonObject();
		o.put("description", getDescription());
		o.put("messages", getMessages());
		o.put("mid", getMid());
		return o;
	}	
}
