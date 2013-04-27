package org.theglobalsquare.ui;

import org.theglobalsquare.framework.ui.TGSListFragment;
import org.theglobalsquare.framework.values.TGSCommunity;

import android.os.Bundle;

public class ViewCommunityFragment extends TGSListFragment {
	public final static String COMMUNITY = "community";
	public final static String TAG = "ViewCommunity";
	
	private TGSCommunity mCommunity;

	@Override
	public void setArguments(Bundle args) {
		mCommunity = (TGSCommunity)args.getSerializable(COMMUNITY);
		android.util.Log.i(TAG, "got community: " + mCommunity);
		super.setArguments(args);
	}
	
	// TODO display the messages from mCommunity
	
}
