package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;

import android.os.Bundle;


public class OverviewListFragment extends CommunityListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// show "you haven't joined any squares" message when empty
		setEmptyText(getActivity().getString(R.string.noSquaresMsg));
		setListShown(true);
	}
	
}
