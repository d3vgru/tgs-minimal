package org.theglobalsquare.framework;

import org.theglobalsquare.framework.values.*;

import android.support.v4.app.Fragment;
import android.widget.EditText;

public interface ITGSActivity {
	public final static int DRAWER_COUNT_BASE = 4; // number of default tabs
	
	// 
	public final static int DRAWER_SEARCH = 0; // search tab
	public final static int DRAWER_FILES = 1; // my files tab
	public final static int DRAWER_OVERVIEW = 2; // my squares overview
	public final static int DRAWER_MONITOR = 3; // debug monitor
	
	// 
	public final static int DRAWER_COMMUNITY = 4; // view community
	
	// TODO have settings in a fragment instead of using UnifiedPreference
	
	// FIXME make all domain classes be interfaces
	// [TGSCommunity, TGSMessage)
	void addCommunity(TGSCommunity c);
	TGSCommunityList getCommunities();
	void createCommunity(TGSCommunity c);
	void communityCreated(TGSCommunity c);
	void communityUpdated(TGSCommunity c);
	//void populateCommunities(TGSCommunityList l);
	void joinCommunity(TGSCommunity c);
	void leaveCommunity(TGSCommunity c);
	void postMessage(TGSCommunity c, TGSMessage m);
	void submitCommunitySearch(EditText et, Fragment searchFragment);
	// search updates go directly to list fragments
	ITGSActivity getTGSActivity();
	ITGSFacade getTGSFacade();
}
