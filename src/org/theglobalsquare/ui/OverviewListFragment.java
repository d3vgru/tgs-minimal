package org.theglobalsquare.ui;

import android.os.Bundle;


import org.theglobalsquare.app.*;
import org.theglobalsquare.framework.*;

public class OverviewListFragment extends TGSListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// show "you haven't joined any squares" message when empty
		setEmptyText(getActivity().getString(R.string.noSquaresMsg));
		setListShown(true);
	}

}
