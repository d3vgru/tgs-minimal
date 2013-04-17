package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.*;

public class TGSListEvent extends TGSEvent {
	public final static String UPDATE = "update";
	
	// not sure if this is that useful but whatever
	ITGSObject subject;

	@Override
	public ITGSObject getSubject() {
		return subject;
	}

	@Override
	public void setSubject(ITGSObject subject) {
		this.subject = subject;
	}
	
	ITGSList list;
	
	public ITGSList getList() {
		return list;
	}
	
	public void setList(ITGSList list) {
		this.list = list;
	}

	public TGSListEvent() {
		type = "list";
	}
	
	public static TGSListEvent forUpdate(ITGSList l) {
		TGSListEvent e = new TGSListEvent();
		e.setList(l);
		e.setVerb(TGSListEvent.UPDATE);
		return e;
	}
	
	public ITGSList emptyList() {
		if(list == null)
			return new TGSObjectList();
		return list.emptyList(); 
	}

}
