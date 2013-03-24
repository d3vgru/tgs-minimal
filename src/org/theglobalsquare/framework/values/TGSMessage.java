package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.TGSObject;

public class TGSMessage extends TGSObject {
	// verbs
	public static final String SEND = "send";
	public static final String SENT = "sent";
	public static final String RECEIVED = "received";
	// system events
	public static final String START = "start";
	public static final String STOP = "stop";
	public static final String LOG = "log";
	
	// who sent it?
	private TGSUser from;
	
	public TGSUser getFrom() {
		return from;
	}

	public void setFrom(TGSUser from) {
		this.from = from;
	}

	// what does it say?
	private String body;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	// what community is this in?
	private TGSCommunity community;

	public TGSCommunity getCommunity() {
		return community;
	}

	public void setCommunity(TGSCommunity community) {
		this.community = community;
	}
	
	// who or what is the intended recipient?
	private TGSObject destination;
	

	public TGSObject getDestination() {
		return destination;
	}

	public void setDestination(TGSObject destination) {
		assert destination instanceof TGSUser || destination instanceof TGSCommunity;
		this.destination = destination;
	}

	public TGSMessage() {
		
	}
	
}
