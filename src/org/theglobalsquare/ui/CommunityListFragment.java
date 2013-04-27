package org.theglobalsquare.ui;

import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ITGSActivity;
import org.theglobalsquare.framework.ITGSObject;
import org.theglobalsquare.framework.ui.TGSListAdapter;
import org.theglobalsquare.framework.ui.TGSListFragment;
import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

// common super class for community search results and "My Squares" (overview) frags
public abstract class CommunityListFragment extends TGSListFragment implements PropertyChangeListener {
	public final static String TAG = "CommunityList";

	/* FIXME remove
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
	}
	*/
		
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setListShown(false);

		// create list adapter
		setListAdapter(createListAdapter());
				
		// handle when the user selects a list item
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewCommunityFragment.showFromList(CommunityListFragment.this, position);
			}
			
		});
		
		setListShown(false);
	}

	protected TGSListAdapter createListAdapter() {
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
	
	public void addCommunities(TGSCommunityList l) {
		if(l == null)
			return;
		Iterator<ITGSObject> i = l.iterator();
		//int added = 0;
		while(i.hasNext()) {
			//added++;
			ITGSObject o = i.next();
			if(o instanceof TGSCommunity) {
				TGSCommunity c = (TGSCommunity)o;
				android.util.Log.i(TAG, "adding community: " + c);
				android.util.Log.i(TAG, "IAMA " + this.getClass().getName());
				// add communities to the activity's community list
				getTGSActivity().addCommunity(c);
			} else {
				android.util.Log.e(TAG, "object is not a TGSCommunity: " + o);
				return;
			}
		}
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				android.util.Log.i(TAG, "notifyDataSetChanged()");
				getTGSListAdapter().notifyDataSetChanged();
				setListShown(true);
			}
		});
	}

}
