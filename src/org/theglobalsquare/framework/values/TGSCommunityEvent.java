package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.*;

public class TGSCommunityEvent extends TGSEvent {
	// verbs:
	// new - created new square [java]
	// joined - joined a square [java]
	// left - left a square [java]
	// load - request data [java]
	
	private TGSCommunity subject;

	@Override
	public TGSCommunity getSubject() {
		return subject;
	}

	@Override
	public void setSubject(ITGSObject subject) {
		assert subject instanceof TGSCommunity;
		this.subject = (TGSCommunity)subject;
	}

	public TGSCommunityEvent() {
		type = "community";
	}
	
	public static TGSCommunityEvent forCreateSquare(TGSCommunity c) {
		TGSCommunityEvent e = new TGSCommunityEvent();
		e.setSubject(c);
		e.setVerb(TGSCommunity.CREATE);
		return e;
	}
	
	public static TGSCommunityEvent forJoinSquare(TGSCommunity c) {
		TGSCommunityEvent e = new TGSCommunityEvent();
		e.setSubject(c);
		e.setVerb(TGSCommunity.JOIN);
		return e;
	}
	
	public static TGSCommunityEvent forLeaveSquare(TGSCommunity c) {
		TGSCommunityEvent e = new TGSCommunityEvent();
		e.setSubject(c);
		e.setVerb(TGSCommunity.LEAVE);
		return e;
	}
	
	public static TGSCommunityEvent forSquareLoad(TGSCommunity c) {
		TGSCommunityEvent e = new TGSCommunityEvent();
		e.setSubject(c);
		e.setVerb(TGSCommunity.LOAD);
		return e;
	}

	public static TGSCommunityEvent forCreatedSquare(TGSCommunity c) {
		TGSCommunityEvent e = new TGSCommunityEvent();
		e.setSubject(c);
		e.setVerb(TGSCommunity.CREATED);
		return e;
	}
	
	public static TGSCommunityEvent forJoinedSquare(TGSCommunity c) {
		TGSCommunityEvent e = new TGSCommunityEvent();
		e.setSubject(c);
		e.setVerb(TGSCommunity.JOINED);
		return e;
	}
	
	public static TGSCommunityEvent forLeftSquare(TGSCommunity c) {
		TGSCommunityEvent e = new TGSCommunityEvent();
		e.setSubject(c);
		e.setVerb(TGSCommunity.LEFT);
		return e;
	}
	
	public static TGSCommunityEvent forSquareData(TGSCommunity c) {
		TGSCommunityEvent e = new TGSCommunityEvent();
		e.setSubject(c);
		e.setVerb(TGSCommunity.DATA);
		return e;
	}

	@Override
	public ITGSList emptyList() {
		return new TGSCommunityList();
	}

	@Override
	public ITGSObject emptyObject() {
		return new TGSCommunity();
	}

}
