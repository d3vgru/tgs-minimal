package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.TGSObject;

public class TGSUser extends TGSObject {
	// verbs
	public static final String NAME_CHANGE = "name_change";
	public static final String NAME_CHANGED = "name_changed";
	
	public TGSUser() {
		setName("Anon");
	}
	
	public static TGSUser _me = null;
	
	public static TGSUser getMe() {
		if(_me == null)
			_me = new TGSUser();
		return _me;
	}
}
