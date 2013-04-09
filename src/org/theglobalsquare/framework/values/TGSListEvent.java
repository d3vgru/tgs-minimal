package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.*;

public class TGSListEvent extends TGSEvent {
	private TGSObjectList subject;

	@Override
	public ITGSObject getSubject() {
		return subject;
	}

	@Override
	public void setSubject(ITGSObject subject) {
		assert subject instanceof TGSObjectList;
		this.subject = (TGSObjectList)subject;
	}

}
