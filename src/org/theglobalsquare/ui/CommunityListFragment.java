package org.theglobalsquare.ui;

import java.util.Iterator;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ITGSActivity;
import org.theglobalsquare.framework.ITGSObject;
import org.theglobalsquare.framework.TGSListAdapter;
import org.theglobalsquare.framework.TGSListFragment;
import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class CommunityListFragment extends TGSListFragment {
	public final static String TAG = "CommunityList";

	public final static String VIEW_COMMUNITY = "view community";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// create list adapter
		setListAdapter(createListAdapter());
				
		// handle when the user selects a list item
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// open a ViewCommunityFragment for the selected community
				ViewCommunityFragment viewCommunity = new ViewCommunityFragment();
				Bundle args = new Bundle();
				args.putSerializable(ViewCommunityFragment.COMMUNITY,
						(TGSCommunity)CommunityListFragment.this.getListAdapter().getItem(position));
				viewCommunity.setArguments(args);
				
				// FIXME see if a new tab needs to be added for this community, then switch to it
				FragmentTransaction ft = CommunityListFragment.this.getFragmentManager().beginTransaction();
				ft.add(viewCommunity, VIEW_COMMUNITY);
				ft.commit();
			}
			
		});
	}
	
	public TGSListAdapter createListAdapter() {
		return new TGSListAdapter(getActivity(), R.layout.community_item, R.id.communityName) {
			@Override
			public TGSCommunityList getItems() {
				Activity activity = getActivity();
				if(!(activity instanceof ITGSActivity)) {
					return new TGSCommunityList();
				}
				ITGSActivity a = (ITGSActivity)activity;
				return a.getCommunities();
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	public void addCommunities(TGSCommunityList l) {
		if(l == null)
			return;
		Iterator<ITGSObject> i = l.iterator();
		ArrayAdapter<ITGSObject> a = (ArrayAdapter<ITGSObject>)getListAdapter();
		int added = 0;
		while(i.hasNext()) {
			added++;
			ITGSObject o = i.next();
			android.util.Log.i(TAG, "adding community: " + o);
			a.add(o);
		}
		if(added > 0) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getTGSListAdapter().notifyDataSetChanged();
					setListShown(true);
				}
			});
		}
	}

}
