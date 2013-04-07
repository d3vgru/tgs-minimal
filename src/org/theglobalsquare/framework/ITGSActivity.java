package org.theglobalsquare.framework;

import org.theglobalsquare.framework.values.*;

import android.support.v4.app.Fragment;
import android.widget.EditText;

public interface ITGSActivity {
	void createCommunity(TGSCommunity c);
	void joinCommunity(TGSCommunity c);
	void leaveCommunity(TGSCommunity c);
	void postMessage(TGSCommunity c, TGSMessage m);
	void submitCommunitySearch(EditText et, Fragment searchFragment);
	// search updates go directly to list fragments	
}
