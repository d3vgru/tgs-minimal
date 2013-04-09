package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.*;

public class TGSSystemEvent extends TGSEvent {
	// verbs:
	// log [python]
	// start [python]
	// stop [python]

	private TGSMessage subject;

	@Override
	public TGSMessage getSubject() {
		return subject;
	}

	@Override
	public void setSubject(ITGSObject subject) {
		assert subject instanceof TGSMessage;
		this.subject = (TGSMessage)subject;
	}
	
	public TGSSystemEvent() {
		type = "system";
	}
	
	public static TGSSystemEvent forLog(TGSMessage m) {
		TGSSystemEvent e = new TGSSystemEvent();
		e.setSubject(m);
		e.setVerb(TGSMessage.LOG);
		return e;
	}
	
	public static TGSSystemEvent forStart() {
		TGSSystemEvent e = new TGSSystemEvent();
		e.setVerb(TGSMessage.START);
		return e;
	}

	public static TGSSystemEvent forStop() {
		TGSSystemEvent e = new TGSSystemEvent();
		e.setVerb(TGSMessage.STOP);
		return e;
	}
}
