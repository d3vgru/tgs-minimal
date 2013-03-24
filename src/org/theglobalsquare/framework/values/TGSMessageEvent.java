package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.*;

public class TGSMessageEvent extends TGSEvent {
	// verbs:
	// received - message has been received
	// send - send this message
	// sent - message has been sent

	private TGSMessage subject;

	@Override
	public TGSMessage getSubject() {
		return subject;
	}

	@Override
	public void setSubject(TGSObject subject) {
		assert subject instanceof TGSMessage;
		this.subject = (TGSMessage)subject;
	}
	
	public TGSMessageEvent() {
		type = "message";
	}
	
	public static TGSMessageEvent forSend(TGSMessage m) {
		TGSMessageEvent e = new TGSMessageEvent();
		e.setSubject(m);
		e.setVerb(TGSMessage.SEND);
		return e;
	}

	public static TGSMessageEvent forSent(TGSMessage m) {
		TGSMessageEvent e = new TGSMessageEvent();
		e.setSubject(m);
		e.setVerb(TGSMessage.SENT);
		return e;
	}

	public static TGSMessageEvent forReceived(TGSMessage m) {
		TGSMessageEvent e = new TGSMessageEvent();
		e.setSubject(m);
		e.setVerb(TGSMessage.RECEIVED);
		return e;
	}
}
