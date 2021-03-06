package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.ITGSList;
import org.theglobalsquare.framework.ITGSObject;
import org.theglobalsquare.framework.TGSSearchEvent;

public class TGSMessageSearchEvent extends TGSSearchEvent {
	// verbs
	// start [java]
	// cancel [java]
	// update [python]
	
	private TGSMessage subject;
	
	@Override
	public TGSMessage getSubject() {
		return subject;
	}

	@Override
	public void setSubject(ITGSObject subject) {
		assert subject instanceof TGSMessage;
		subject = (TGSMessage)subject;
	}
	
	public TGSMessageSearchEvent() {
		type += ".message";
	}
	
	public static TGSMessageSearchEvent forStart(TGSMessage m) {
		TGSMessageSearchEvent e = new TGSMessageSearchEvent();
		e.setSubject(m);
		e.setVerb(TGSSearchEvent.START);
		return e;
	}

	public static TGSMessageSearchEvent forCancel(TGSMessage m) {
		TGSMessageSearchEvent e = new TGSMessageSearchEvent();
		e.setSubject(m);
		e.setVerb(TGSSearchEvent.CANCEL);
		return e;
	}

	public static TGSMessageSearchEvent forUpdate(TGSMessage m) {
		TGSMessageSearchEvent e = new TGSMessageSearchEvent();
		e.setSubject(m);
		e.setVerb(TGSSearchEvent.UPDATE);
		return e;
	}
	
	@Override
	public ITGSList emptyList() {
		return new TGSMessageList();
	}

	@Override
	public ITGSObject emptyObject() {
		return new TGSMessage();
	}

}
