package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.TGSObjectList;

public class TGSCommunityList extends TGSObjectList {
	// serializable
	private static final long serialVersionUID = 3915592665695928923L;
	
	public void addCommunity(TGSCommunity c) {
		int pos = indexOf(c);
		if(pos == -1) {
			// add
			add(c);
		} else {
			// ..or replace
			remove(pos);
			add(pos, c);
		}
	}

	@Override
	public boolean contains(Object object) {
		return indexOf(object) > -1;
	}

	@Override
	public int indexOf(Object object) {
		if(object instanceof TGSCommunity && object != null) {
			TGSCommunity oc = (TGSCommunity)object;
			// FIXME is this a good enough comparison?
			String cid = oc.getCid();
			if(cid == null)
				return -1;
			for(int i=0; i<size(); i++) {
				TGSCommunity c = (TGSCommunity)get(i);
				if(cid.equals(c.getCid()))
					return i;
			}
		}
		return -1;
	}
	
	public TGSCommunity get(String cid) {
		TGSCommunity c = new TGSCommunity();
		c.setCid(cid);
		int index = indexOf(c);
		if(index == -1)
			return null;
		return (TGSCommunity) get(index);
	}
	
}
