package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.TGSObjectList;

public class TGSUserList extends TGSObjectList {
	// serializable
	private static final long serialVersionUID = 5119029923303540285L;

	public void addUser(TGSUser u) {
		add(u);
	}
}
