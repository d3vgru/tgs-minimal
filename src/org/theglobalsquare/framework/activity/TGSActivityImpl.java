package org.theglobalsquare.framework.activity;

import org.theglobalsquare.app.Facade;
import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ITGSActivity;
import org.theglobalsquare.framework.TGSListFragment;
import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityEvent;
import org.theglobalsquare.framework.values.TGSCommunitySearchEvent;
import org.theglobalsquare.framework.values.TGSMessage;
import org.theglobalsquare.framework.values.TGSSearchEvent;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public abstract class TGSActivityImpl extends TGSUIActivity implements ITGSActivity {
	public final static String TAG = "TGSActivity";
	
	// ITGSActivity impl
	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void createCommunity(TGSCommunity c) {
		android.util.Log.i(TAG, "creating: " + c);
		
		// make create community event
		TGSCommunityEvent e = TGSCommunityEvent.forCreateSquare(c);

		// not sure subject will work with current pyjnius
		e.setObject(c);

		// put it in the event queue
		Facade.sendEvent(e, true);
	}
	
	@Override
	public void submitCommunitySearch(EditText et, Fragment searchFragment) {
		String term = "";
		Editable e = et.getText();
		if(e != null)
			term = e.toString();
		et.setText(null);
		dismissKeyboardFor(this, et);
		
		if(term == null)
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
// jnius can't seem to access the subject field
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