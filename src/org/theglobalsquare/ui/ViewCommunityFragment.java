package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ui.TGSListFragment;
import org.theglobalsquare.framework.values.TGSCommunity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ViewCommunityFragment extends TGSListFragment {
	public final static String TAG = "ViewCommunity";

	public final static String COMMUNITY = "community";
	public final static String VIEW_COMMUNITY = "view community";
	
	private TGSCommunity mCommunity;

	@Override
	public void setArguments(Bundle args) {
		mCommunity = (TGSCommunity)args.getSerializable(COMMUNITY);
		android.util.Log.i(TAG, "got community: " + mCommunity);
		super.setArguments(args);
	}
	
	// TODO display the messages from mCommunity
	
	
	public static void showFromList(CommunityListFragment parent, int position) {
		TGSCommunity c = (TGSCommunity)parent.getListAdapter().getItem(position);
		show(parent.getFragmentManager(), c);
	}
	
	public static void show(FragmentManager fragMgr, TGSCommunity c) {
		// open a ViewCommunityFragment for the selected community
		ViewCommunityFragment viewCommunity = new ViewCommunityFragment();
		Bundle args = new Bundle();
		args.putSerializable(ViewCommunityFragment.COMMUNITY, c);
		viewCommunity.setArguments(args);
		
		FragmentTransaction ft = fragMgr.beginTransaction();
		ft.add(R.id.mainContentFragment, viewCommunity, VIEW_COMMUNITY);
		ft.commit();
	}
}
