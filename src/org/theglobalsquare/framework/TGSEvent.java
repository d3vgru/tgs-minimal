package org.theglobalsquare.framework;

public abstract class TGSEvent {
	// what kind of event is this?
	protected String type = "abstract";
	public String getType() { return type; }
	
	// what is doing it?
	public abstract TGSObject getSubject();
	public abstract void setSubject(TGSObject subject);
	
	// what is going on?
	private String verb = "chilling";
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}

	// to what is it happening?
	private TGSObject object = null;
	public TGSObject getObject() {
		return object;
	}
	public void setObject(TGSObject object) {
		this.object = object;
	}
	
	public String toString() {
		String out = (getSubject() + " " + getVerb());
		TGSObject o = getObject();
		if(o != null)
			out += " " + o;
		return out;
	}
}
