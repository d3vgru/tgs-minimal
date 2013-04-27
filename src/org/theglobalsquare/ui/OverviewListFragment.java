package org.theglobalsquare.ui;

import java.beans.PropertyChangeEvent;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ITGSObject;
import org.theglobalsquare.framework.TGSEvent;
import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityEvent;
import org.theglobalsquare.framework.values.TGSCommunityList;

import android.os.Bundle;
import android.view.View;


public class OverviewListFragment extends CommunityListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// show "you haven't joined any squares" message when empty
		setEmptyText(getActivity().getString(R.string.noSquaresMsg));
		// FIXME we don't really know if there are no communities or not
		// make the python side send an event when it finishes loading communities (even if there are none)
		//setListShown(true);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// register for community updates
		getFacade().addListener(TGSCommunityEvent.class, this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if(getView() == null) {
			android.util.Log.w(TAG, "no view while updating overview");
			return;
		}
		
		// handle search (and message?) update events coming from python side
		android.util.Log.i(TAG, "propertyChange: " + event + ", new value: " + event.getNewValue());
		Object o = event.getNewValue();
		if(o instanceof TGSEvent) {
			final TGSEvent e = (TGSEvent)o;
			android.util.Log.i(TAG, "event: " + e.getClass().getName());
			
			if(e instanceof TGSCommunityEvent) {
				android.util.Log.i(TAG, "got community event");
				ITGSObject c = e.getSubject();
				if(c == null) {
					android.util.Log.w(TAG, "community was null");
					return;
				}
				if(c instanceof TGSCommunity) {
					TGSCommunityList l = new TGSCommunityList();
					l.addCommunity((TGSCommunity)c);
					OverviewListFragment.this.addCommunities(l);
				} else {
					android.util.Log.i(TAG, "subject was not a TGSCommunity");
				}
			}
		} else {
			android.util.Log.i(TAG, "not event: " + o.getClass().getName());
		}
	}

}
