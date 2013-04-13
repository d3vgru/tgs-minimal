package org.theglobalsquare.framework;

import org.json.*;

public interface ITGSObject {
	String getName();
	void setName(String name);
	String toJson() throws JSONException;
}
