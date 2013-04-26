package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;

import android.os.Bundle;


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
	
}
