package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.ITGSList;
import org.theglobalsquare.framework.ITGSObject;
import org.theglobalsquare.framework.TGSSearchEvent;

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
	public void setSubject(ITGSObject subject) {
		assert subject instanceof TGSCommunity;
		subject = (TGSCommunity)subject;
	}
	
	public TGSCommunitySearchEvent() {
		type += ".community";
	}

	@Override
	public ITGSList emptyList() {
		return new TGSCommunityList();
	}

	@Override
	public ITGSObject emptyObject() {
		return new TGSCommunity();
	}

}
