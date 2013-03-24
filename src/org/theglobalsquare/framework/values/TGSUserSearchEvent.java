package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.TGSObject;

public class TGSUserSearchEvent extends TGSSearchEvent {
	// verbs
	// start [java]
	// cancel [java]
	// update [python]

	private TGSUser subject;
	
	@Override
	public TGSUser getSubject() {
		return subject;
	}

	@Override
	public void setSubject(TGSObject subject) {
		assert subject instanceof TGSUser;
		subject = (TGSUser)subject;
	}
	
	public TGSUserSearchEvent() {
		type += ".user";
	}
}
