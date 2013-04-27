package org.theglobalsquare.ui;

import java.beans.PropertyChangeEvent;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ITGSList;
import org.theglobalsquare.framework.ITGSObject;
import org.theglobalsquare.framework.TGSEvent;
import org.theglobalsquare.framework.ui.TGSListAdapter;
import org.theglobalsquare.framework.TGSObjectList;
import org.theglobalsquare.framework.TGSSearchEvent;
import org.theglobalsquare.framework.values.TGSCommunityList;
import org.theglobalsquare.framework.values.TGSCommunitySearchEvent;

import android.os.Bundle;

public class SearchResultsFragment extends CommunityListFragment {
	public final static String TAG = "SearchResults";

	private String mSearchTerms;
	
	public void setSearchTerms(String searchTerms) {
		mSearchTerms = searchTerms;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getString(R.string.searchNoResultsLabel));
	}

	@Override
	public TGSListAdapter createListAdapter() {
		// FIXME this is coupled to communities
		return new TGSListAdapter(getActivity(), R.layout.community_item, R.id.communityName) {
			private TGSObjectList mResults = new TGSObjectList();
			
			@Override
			public TGSObjectList getItems() {
				return mResults;
			}
		};
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if(getView() == null) {
			android.util.Log.w(TAG, "no view while updating terms: " + mSearchTerms);
			return;
		}
		
		// handle search (and message?) update events coming from python side
		android.util.Log.i(TAG, "propertyChange: " + event + ", new value: " + event.getNewValue());
		android.util.Log.i(TAG, "search terms: " + mSearchTerms);
		Object o = event.getNewValue();
		if(o instanceof TGSEvent) {
			final TGSEvent e = (TGSEvent)o;
			android.util.Log.i(TAG, "event: " + e.getClass().getName());
			// handle actual results
			if(e instanceof TGSSearchEvent) {
				android.util.Log.i(TAG, "got search event");
				ITGSObject c = e.getSubject();
				if(c == null) {
					android.util.Log.w(TAG, "terms were null");
					return;
				}
				String terms = c.getName();
				if(terms == null || !terms.equals(mSearchTerms)) {
					android.util.Log.w(TAG, "terms in event (" + terms + ") didn't match: " + mSearchTerms);
					return;
				}
				if(e instanceof TGSCommunitySearchEvent) {
					android.util.Log.i(TAG, "got community search event");
					TGSCommunitySearchEvent communitySearch = (TGSCommunitySearchEvent)e;
					ITGSList searchResults = communitySearch.getList();
					if(searchResults instanceof TGSCommunityList) {
						android.util.Log.i(TAG, "got community list");
						TGSCommunityList communityList = (TGSCommunityList)searchResults;
						SearchResultsFragment.this.addCommunities(communityList);
					}
				}
				
				// show "your search didn't return any results" message if empty
				// superclass should take care of this
				//setListShown(true);
			}
		} else {
			android.util.Log.i(TAG, "not event: " + o.getClass().getName());
		}
//		TGSMainActivity.handle(event.getNewValue());
	}
}
