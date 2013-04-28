package org.theglobalsquare.framework.activity;

import java.io.File;

import org.theglobalsquare.app.R;
import org.theglobalsquare.app.config.EditPreferences;
import org.theglobalsquare.framework.ITGSActivity;

import android.content.Intent;
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
	
	public static int updateViewIdForDrawer(TGSDrawerActivity a, int drawer, boolean showFragment) {
		int adjustedDrawer = drawer;
		int newActiveView = -1;
		if(ITGSActivity.DRAWER_SEARCH != drawer) {
			TGSBaseActivity.dismissKeyboardFor(a, (EditText)a.findViewById(R.id.txt_search_terms));
		}
		switch(drawer) {
			case ITGSActivity.DRAWER_SEARCH:
				newActiveView = R.id.searchDrawer;
				if(showFragment)
					a.showSearch();
				break;
			case ITGSActivity.DRAWER_FILES:
				newActiveView = R.id.fileDrawer;
				if(showFragment)
					a.showFiles();
				break;
			case ITGSActivity.DRAWER_OVERVIEW:
				// TODO eventually point to the actual community button
				if(showFragment)
					a.showOverview();
			case ITGSActivity.DRAWER_COMMUNITY:
				adjustedDrawer = ITGSActivity.DRAWER_OVERVIEW;
				newActiveView = R.id.overviewDrawer;
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
				newActiveView = R.id.helpDrawer;
				if(showFragment)
					a.showHelp();
				break;
			case ITGSActivity.DRAWER_ABOUT:
				newActiveView = R.id.aboutDrawer;
				if(showFragment)
					a.showAbout();
				break;
			case ITGSActivity.DRAWER_MONITOR:
				newActiveView = R.id.monitorDrawer;
				if(showFragment)
					a.showMonitor();
				break;
			default:
				break;
		}
		a.setActiveViewId(newActiveView);
		if(newActiveView > -1)
			a.updateActiveDrawer();
		a.closeDrawer();
		return adjustedDrawer;
	}

	// TODO get rid of this
	public static void monitorHomeDir(TGSDrawerActivity a) {
		File path = new File(a.getFilesDir().getAbsolutePath() + "/");
		if(!path.exists())
			return;
		a.monitor("listing files/(D)irectories in " + path.getAbsolutePath());
		File[] files = path.listFiles();
		if(files != null) {
			for(int i=0; i<files.length; i++) {
				File f = files[i];
				a.monitor(
						(f.isDirectory() ? "(D) " : "")
							+ f.getPath()
				);
			}
		}
	}

}
