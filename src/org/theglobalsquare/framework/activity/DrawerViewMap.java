package org.theglobalsquare.framework.activity;

import org.theglobalsquare.app.R;
import org.theglobalsquare.app.config.EditPreferences;
import org.theglobalsquare.framework.ITGSActivity;
import org.theglobalsquare.ui.AboutFragment;
import org.theglobalsquare.ui.FilesListFragment;
import org.theglobalsquare.ui.MonitorFragment;
import org.theglobalsquare.ui.OverviewListFragment;
import org.theglobalsquare.ui.SearchFragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.EditText;

public class DrawerViewMap {
	public static int getDrawerForViewId(int vid) {
		int drawer = -1;
		switch(vid) {
			case R.id.searchDrawer:
				drawer = ITGSActivity.DRAWER_SEARCH;
				break;
			case R.id.fileDrawer:
				drawer = ITGSActivity.DRAWER_FILES;
				break;
			case R.id.overviewDrawer:
				drawer = ITGSActivity.DRAWER_OVERVIEW;
				break;
			case ITGSActivity.DRAWER_COMMUNITY:
				// TODO figure out what to actually do here
				drawer = -2;
				break;
			case R.id.settingsDrawer:
				drawer = ITGSActivity.DRAWER_SETTINGS;
				break;
			case R.id.helpDrawer:
				drawer = ITGSActivity.DRAWER_HELP;
				break;
			case R.id.aboutDrawer:
				drawer = ITGSActivity.DRAWER_ABOUT;
				break;
			case R.id.monitorDrawer:
				drawer = ITGSActivity.DRAWER_MONITOR;
				break;
			default:
				break;
		}
		return drawer;
	}
	
	public static int getAdjustedDrawer(TGSDrawerActivity a, int drawer, boolean showFragment) {
		int adjustedDrawer = drawer;
		if(ITGSActivity.DRAWER_SEARCH != drawer) {
			TGSBaseActivity.dismissKeyboardFor(a, (EditText)a.findViewById(R.id.txt_search_terms));
		}
		Fragment f = null;
		String tag = null; // names are important, correspond to tag+"Drawer" to find id of buttons
		switch(drawer) {
			case ITGSActivity.DRAWER_SEARCH:
				f = new SearchFragment();
				tag = "search";
				break;
			case ITGSActivity.DRAWER_FILES:
				f = new FilesListFragment();
				tag = "file";
				break;
			case ITGSActivity.DRAWER_OVERVIEW:
				f = new OverviewListFragment();
				tag =  "overview";
			case ITGSActivity.DRAWER_COMMUNITY:
				// TODO eventually point to the actual community button if it's a community
				// or maybe not..
				adjustedDrawer = ITGSActivity.DRAWER_OVERVIEW;
				break;
			case ITGSActivity.DRAWER_SETTINGS:
				// TODO maybe attach drawer to prefs activity
				// don't track this since it starts a new activity entirely
				adjustedDrawer = a.getSelectedDrawer();
				//newActiveView = R.id.settingsDrawer;
				// show settings
				if(showFragment) {
					Intent prefsIntent = new Intent();
					prefsIntent.setClass(a, EditPreferences.class);
					a.startActivityForResult(prefsIntent, ITGSActivity.PREFERENCES);
				}
				break;
			case ITGSActivity.DRAWER_HELP:
				/* TODO eventually..
				f = new HelpFragment();
				tag = "help";
				*/
				break;
			case ITGSActivity.DRAWER_ABOUT:
				f = new AboutFragment();
				tag =  "about";
				break;
			case ITGSActivity.DRAWER_MONITOR:
				f = new MonitorFragment();
				tag = "monitor";
				break;
			default:
				break;
		}
		
		if(showFragment && f != null)
			a.showItem(f, tag);

		a.closeDrawer();
		return adjustedDrawer;
	}

}
