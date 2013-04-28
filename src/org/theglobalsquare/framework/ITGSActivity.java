package org.theglobalsquare.framework;

import org.theglobalsquare.framework.values.*;

import android.support.v4.app.Fragment;
import android.widget.EditText;

public interface ITGSActivity {
	public final static int DRAWER_COUNT_BASE = 3; // number of default screens
	
	// default drawers
	public final static int DRAWER_SEARCH = 0; // search screen
	public final static int DRAWER_FILES = 1; // my files screen
	public final static int DRAWER_OVERVIEW = 2; // my squares overview
	
	// additional drawers
	public final static int DRAWER_COMMUNITY = 3; // view community
	
	// TODO have settings in a fragment instead of using UnifiedPreference
	public final static int DRAWER_SETTINGS = 4; // settings
	public final static int DRAWER_HELP = 5; // debug monitor
	public final static int DRAWER_MONITOR = 6; // debug monitor
	public final static int DRAWER_ABOUT = 7; // about
	
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
