package org.theglobalsquare.framework.values;

import java.util.*;

import org.theglobalsquare.framework.TGSObject;

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

	private List<TGSMessage> messages;
	
	public List<TGSMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<TGSMessage> messages) {
		this.messages = messages;
	}

	public TGSCommunity() {
		this.messages = new ArrayList<TGSMessage>();
	}
	
	
	
	public String toString() {
		return getName() + " (" + getMid() + ")";
	}
}
