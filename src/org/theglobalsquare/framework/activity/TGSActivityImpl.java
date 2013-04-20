package org.theglobalsquare.framework.activity;

import org.theglobalsquare.app.Facade;
import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ITGSActivity;
import org.theglobalsquare.framework.TGSListFragment;
import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityEvent;
import org.theglobalsquare.framework.values.TGSCommunityList;
import org.theglobalsquare.framework.values.TGSCommunitySearchEvent;
import org.theglobalsquare.framework.values.TGSMessage;
import org.theglobalsquare.framework.TGSSearchEvent;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public abstract class TGSActivityImpl extends TGSUIActivity
		implements ITGSActivity {
	public final static String TAG = "TGSActivity";
	
	private TGSCommunityList mCommunities = new TGSCommunityList();
	
	@Override
	public TGSCommunityList getCommunities() {
		return mCommunities;
	}

	public TGSCommunity getCommunity(String cid) {
		return mCommunities.get(cid);
	}
	
	private void setCommunitiesDirty() {
		// construct tabs as appropriate
		buildCommunityTabs();
	}

	public void setCommunities(TGSCommunityList l) {
		this.mCommunities = l;
		
		// notify there are new communities
		setCommunitiesDirty();
	}
	
	@Override
	public void addCommunity(TGSCommunity c) {
		// add this community if it doesn't exist yet
		mCommunities.addCommunity(c);
		
		// notify there is a new community
		setCommunitiesDirty();
	}
	
	public boolean hasCommunity(TGSCommunity c) {
		if(mCommunities == null)
			return false;
		return mCommunities.contains(c);
	}

	
	// ITGSActivity impl
	@Override
	public Activity getActivity() {
		return this;
	}

	// load communities on startup
	/* not needed since dispersy sends an event for each one
	public void populateCommunities(TGSCommunityList l) {
		// store the initial list of communities
		getFacade().setCommunities(l);
	}
	*/

	@Override
	public void createCommunity(TGSCommunity c) {
		android.util.Log.i(TAG, "creating: " + c);
		
		// return if the name is blank
		// TODO error message when submit clicked
		String name = c.getName();
		if(name == null || "".equals(name))
			return;
		
		// make create community event
		TGSCommunityEvent e = TGSCommunityEvent.forCreateSquare(c);

		// put it in the event queue
		Facade.sendEvent(e, true);
	}
	
	@Override
	public void communityCreated(TGSCommunity c) {
		// process creation of community
		addCommunity(c);
	}
	
	@Override
	public void submitCommunitySearch(EditText et, Fragment searchFragment) {
		String term = "";
		Editable e = et.getText();
		if(e != null)
			term = e.toString();
		et.setText(null);
		dismissKeyboardFor(this, et);
		
		if(term == null || "".equals(term))
			return;

		// manage results in searchFragment
		if(searchFragment != null) {
			// hide search box
			findViewById(R.id.group_search_terms).setVisibility(View.GONE);
			
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			// remove existing results
			if(sSearchResults != null) {
				// unregister with results events
				getFacade().removeListener(TGSSearchEvent.class, sSearchResults);
				
				android.util.Log.i(TAG, "REMOVING listener: " + sSearchResults);

				ft.remove(sSearchResults);
			}
			
			// add new results
			sSearchResults = new TGSListFragment();
			
			if(sSearchResults != null) {
				// register with results events
				getFacade().addListener(TGSCommunitySearchEvent.class, sSearchResults);
				
				android.util.Log.i(TAG, "registering listener: " + sSearchResults);

				ft.add(R.id.layout_search_main, sSearchResults);
			}
			ft.commit();
		}
		
		// make a search event and put it in the queue
		TGSCommunity c = new TGSCommunity();
		c.setName(term);
		TGSSearchEvent s = new TGSCommunitySearchEvent();
// FIXME jnius can't seem to access the subject field
//		s.setSubject(c);
		s.setObject(c);
		s.setVerb(TGSSearchEvent.START);
		Facade.sendEvent(s, true);
		
		android.util.Log.i(TAG, "queued search for: " + term);
	}

	@Override
	public void joinCommunity(TGSCommunity c) {
		// TODO send join community event to python
		
	}

	@Override
	public void leaveCommunity(TGSCommunity c) {
		// TODO send leave community event to python
		
	}

	@Override
	public void postMessage(TGSCommunity c, TGSMessage m) {
		// TODO send post message event to python
		
	}

}
