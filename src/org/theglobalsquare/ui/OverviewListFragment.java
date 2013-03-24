package org.theglobalsquare.ui;

import android.os.Bundle;


import org.theglobalsquare.app.*;
import org.theglobalsquare.framework.MessageListFragment;

public class OverviewListFragment extends MessageListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setEmptyText(getActivity().getString(R.string.noSquaresMsg));
		setListShown(true);
	}

}
