package org.theglobalsquare.framework;

import java.util.ArrayList;

import org.json.*;

public class TGSObjectList extends ArrayList<ITGSObject> implements ITGSObject, ITGSList {
	public final static String UPDATE = "update";
	
	// serializable
	private static final long serialVersionUID = 8005602994593599806L;

	private String name;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	// shouldn't really need to override this..
	public JSONArray toJsonArray() throws JSONException {
		JSONArray out = new JSONArray();
		for(int i=0; i<size(); i++)
			out.put(get(i));
		return out;
	}
	
	@Override
	public String toJson() throws JSONException {
		return toJsonArray().toString();
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
