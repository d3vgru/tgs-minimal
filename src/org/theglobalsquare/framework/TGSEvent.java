package org.theglobalsquare.framework;

import org.json.*;

public abstract class TGSEvent extends TGSObject {
	// what kind of event is this?
	protected String type = "abstract";
	public String getType() { return type; }
	
	// what is doing it?
	public abstract ITGSObject getSubject();
	public abstract void setSubject(ITGSObject subject);
	
	// what is going on?
	private String verb = "chilling";
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}

	// to what is it happening?
	private ITGSObject object = null;
	public ITGSObject getObject() {
		return object;
	}
	public void setObject(ITGSObject object) {
		this.object = object;
	}
	
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject o = super.toJsonObject();
		o.put("type", getType());
		o.put("subject", getSubject());
		o.put("verb", getVerb());
		o.put("object", getObject());
		return o;
	}
	
	public ITGSList emptyList() {
		return new TGSObjectList();
	}

	public ITGSObject emptyObject() {
		return new TGSObject();
	}
}
