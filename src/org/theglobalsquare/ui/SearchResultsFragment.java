package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.TGSListAdapter;
import org.theglobalsquare.framework.TGSObjectList;

import android.os.Bundle;
import android.widget.ListAdapter;

public class SearchResultsFragment extends CommunityListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getString(R.string.searchNoResultsLabel));
	}

	@Override
	public ListAdapter createListAdapter() {
		return new TGSListAdapter(getActivity(), R.layout.community_item, R.id.communityName) {
			private TGSObjectList mResults = new TGSObjectList();
			
			@Override
			public TGSObjectList getItems() {
				return mResults;
			}
		};
	}
	
}
