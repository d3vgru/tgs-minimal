package org.theglobalsquare.framework.values;

import org.json.JSONException;
import org.json.JSONObject;
import org.theglobalsquare.framework.TGSObject;
import org.theglobalsquare.framework.activity.TGSBaseActivity;

public class TGSUser extends TGSObject {
	// verbs
	public static final String NAME_CHANGE = "name_change";
	public static final String NAME_CHANGED = "name_changed";
	
	public TGSUser() {
	}
	
	private static TGSUser _me = null;
	
	public static TGSUser getMe() {
		if(_me == null) {
			_me = new TGSUser();
			_me.setName(TGSBaseActivity.getStaticFacade().getAlias());
		}
		return _me;
	}

	@Override
	public JSONObject toJsonObject() throws JSONException {
		// currently, user just has a name, so super is fine
		// will need these once we build out the user a little more
		JSONObject o = super.toJsonObject();
		
		return o;
	}
	
}
