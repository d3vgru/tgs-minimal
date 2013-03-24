package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.*;

public class TGSUserEvent extends TGSEvent {
	// verbs:
	// name change - the user changed name [java]
	// name changed - user name updated [python]

	private TGSUser subject;

	@Override
	public TGSUser getSubject() {
		return subject;
	}

	@Override
	public void setSubject(TGSObject subject) {
		assert subject instanceof TGSUser;
		this.subject = (TGSUser)subject;
	}
	
	public TGSUserEvent() {
		type = "user";
	}
	
	public static TGSUserEvent forNameChange(TGSUser u) {
		TGSUserEvent e = new TGSUserEvent();
		e.setSubject(u);
		e.setVerb(TGSUser.NAME_CHANGE);
		return e;
	}

	public static TGSUserEvent forNameChanged(TGSUser u) {
		TGSUserEvent e = new TGSUserEvent();
		e.setSubject(u);
		e.setVerb(TGSUser.NAME_CHANGED);
		return e;
	}
}
