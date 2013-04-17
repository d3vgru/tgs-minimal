package org.theglobalsquare.ui;

import java.util.ArrayList;
import java.util.Collection;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import org.theglobalsquare.app.*;
import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityList;

public class OverviewListFragment extends TGSListFragment {
	public final static String VIEW_COMMUNITY = "view community";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// create list adapter
		setListAdapter(new ArrayAdapter<ITGSObject>(this.getActivity(),
				R.layout.community_item, R.id.communityName) {
			
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View row = super.getView(position, convertView, parent);
				TextView name = (TextView)row.findViewById(R.id.communityName);
				ITGSObject o = getItem(position);
				if(name != null) {
					if(o instanceof TGSCommunity) {
						TGSCommunity c = (TGSCommunity)o;
						name.setText(c.getName());
						TextView description = (TextView)row.findViewById(R.id.communityDescription);
						description.setText(c.getDescription());
						
						// TODO set image for avatar
						
					} else {
						name.setText(R.string.errUnrecognizedType);
					}
				}
				return row;
			}
			
			public TGSCommunityList getItems() {
				return ((ITGSActivity)OverviewListFragment.this.getActivity()).getCommunities();
			}

			@Override
			public int getCount() {
				return getItems().size();
			}

			@Override
			public ITGSObject getItem(int position) {
				return getItems().get(position);
			}

			@Override
			public void add(ITGSObject object) {
				getItems().add(object);
			}

			@Override
			public void addAll(Collection<? extends ITGSObject> collection) {
				getItems().addAll(collection);
			}

			@Override
			public void clear() {
				getItems().clear();
			}

			@Override
			public void insert(ITGSObject object, int index) {
				getItems().add(index, object);
			}

			@Override
			public void remove(ITGSObject object) {
				getItems().remove(object);
			}

		});
				
		// handle when the user selects a list item
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// open a ViewCommunityFragment for the selected community
				ViewCommunityFragment viewCommunity = new ViewCommunityFragment();
				Bundle args = new Bundle();
				args.putSerializable(ViewCommunityFragment.COMMUNITY,
						(TGSCommunity)OverviewListFragment.this.getListAdapter().getItem(position));
				viewCommunity.setArguments(args);
				
				// FIXME see if a new tab needs to be added for this community, then switch to it
				FragmentTransaction ft = OverviewListFragment.this.getFragmentManager().beginTransaction();
				ft.add(viewCommunity, VIEW_COMMUNITY);
				ft.commit();
			}
			
		});

		// show "you haven't joined any squares" message when empty
		setEmptyText(getActivity().getString(R.string.noSquaresMsg));
		setListShown(true);
	}
	
	@SuppressWarnings("unchecked")
	public void addCommunities(TGSCommunityList l) {
		((ArrayList<ITGSObject>)getListAdapter()).addAll(l);
	}

}
