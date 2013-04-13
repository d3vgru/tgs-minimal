package org.theglobalsquare.framework;

import org.json.JSONException;
import org.json.JSONObject;

public class TGSObject implements ITGSObject {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	// override (calling super) to add additional keys in descendant classes
	public JSONObject toJsonObject() throws JSONException {
		JSONObject out = new JSONObject();
		out.put("name", name);
		return out;
	}
	
	@Override
	public String toJson() throws JSONException {
		return toJsonObject().toString();
	}
	
	@Override
	public String toString() {
		try {
			return toJson();
		} catch(JSONException ex) {
			return "{error:" + ex.getLocalizedMessage() + "}";
		}
	}
}
