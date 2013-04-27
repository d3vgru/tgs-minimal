package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ui.TGSListFragment;

import android.os.Bundle;

public class CommunityMessagesFragment extends TGSListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// show "private storage empty" message when empty
		setEmptyText(getActivity().getString(R.string.noMessagesMsg));
		setListShown(true);
	}
	
}
