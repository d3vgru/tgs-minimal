package org.theglobalsquare.framework;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public interface ITGSList extends List<ITGSObject> {
	String toJson() throws JSONException;
	JSONArray toJsonArray() throws JSONException;
	ITGSList emptyList();
}
