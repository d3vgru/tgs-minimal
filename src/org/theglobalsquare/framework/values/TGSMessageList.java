package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.TGSObjectList;

public class TGSMessageList extends TGSObjectList {
	// serializable
	private static final long serialVersionUID = 847078911208986674L;

	public void addMessage(TGSMessage m) {
		add(m);
	}
}
