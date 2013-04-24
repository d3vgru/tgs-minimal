package org.theglobalsquare.framework;

import java.beans.*;

import org.theglobalsquare.app.Facade;
import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.values.TGSCommunityList;
import org.theglobalsquare.framework.values.TGSCommunitySearchEvent;
import org.theglobalsquare.ui.CommunityListFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;

public class TGSListFragment extends SherlockListFragment implements PropertyChangeListener {
	public final static String TAG = "TGSList";
	
	public Facade getFacade() {
		return (Facade)getActivity().getApplication();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		updateEmptyText(getString(R.string.emptyListLabel));
		return v;
	}
	
	public void updateEmptyText(final String emptyText) {
		Activity activity = getActivity();
		if(activity != null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(getView() == null)
						return;
					setEmptyText(emptyText);
					setListShown(true);
				}	
			});
		}
		else android.util.Log.d(TAG, "activity null");
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// handle search (and message?) update events coming from python side
		android.util.Log.i(TAG, "propertyChange: " + event + ", new value: " + event.getNewValue());
		Object o = event.getNewValue();
		if(o instanceof TGSEvent) {
			final TGSEvent e = (TGSEvent)o;
			android.util.Log.i(TAG, "event: " + e.getClass().getName());
			//updateEmptyText(e.toString());
			// handle actual results
			if(e instanceof TGSSearchEvent) {
				if(e instanceof TGSCommunitySearchEvent
						&& this instanceof CommunityListFragment) {
					TGSCommunitySearchEvent communitySearch = (TGSCommunitySearchEvent)e;
					ITGSList searchResults = communitySearch.getList();
					if(searchResults instanceof TGSCommunityList) {
						TGSCommunityList communityList = (TGSCommunityList)searchResults;
						CommunityListFragment communityListFrag = (CommunityListFragment)this;
						communityListFrag.addCommunities(communityList);
					}
				}
				
				// show "your search didn't return any results" message if empty
				setListShown(true);
			}
		} else {
			android.util.Log.i(TAG, "not event: " + o.getClass().getName());
		}
//		TGSMainActivity.handle(event.getNewValue());
	}

}
