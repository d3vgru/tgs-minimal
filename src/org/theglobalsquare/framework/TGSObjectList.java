package org.theglobalsquare.framework;

import java.util.ArrayList;

public class TGSObjectList extends ArrayList<ITGSObject> implements ITGSObject {
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

}
