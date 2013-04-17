package org.theglobalsquare.framework.values;

import org.json.JSONException;
import org.json.JSONObject;
import org.theglobalsquare.framework.ITGSObject;
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
	
	// what is it about?
	private String subject;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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
	private ITGSObject destination;
	

	public ITGSObject getDestination() {
		return destination;
	}

	public void setDestination(ITGSObject destination) {
		assert destination instanceof TGSUser || destination instanceof TGSCommunity;
		this.destination = destination;
	}

	public TGSMessage() {
		
	}
	
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject o = super.toJsonObject();
		TGSUser f = getFrom();
		if(f != null)
			o.put("from", f.toJsonObject());
		o.put("subject", getSubject());
		o.put("body", getBody());
		TGSCommunity c = getCommunity();
		if(c != null)
			o.put("community", c.toJsonObject());
		ITGSObject d = getDestination();
		if(d != null)
			o.put("destination", d.toJsonObject());
		return o;
	}
	
}
