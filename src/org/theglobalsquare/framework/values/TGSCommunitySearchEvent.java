package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.TGSObject;

public class TGSCommunitySearchEvent extends TGSSearchEvent {
	// verbs
	// start [java]
	// cancel [java]
	// update [python]

	private TGSCommunity subject;
	
	@Override
	public TGSCommunity getSubject() {
		return subject;
	}

	@Override
	public void setSubject(TGSObject subject) {
		assert subject instanceof TGSCommunity;
		subject = (TGSCommunity)subject;
	}
	
	public TGSCommunitySearchEvent() {
		type += ".user";
	}
}
