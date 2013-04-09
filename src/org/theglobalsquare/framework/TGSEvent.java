package org.theglobalsquare.framework;

public abstract class TGSEvent {
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
	
	public String toString() {
		String out = getVerb();
		Object s = getSubject();
		if(s != null)
			out = s.toString() + " " + out;
		ITGSObject o = getObject();
		if(o != null)
			out += " " + o;
		return out;
	}
}
