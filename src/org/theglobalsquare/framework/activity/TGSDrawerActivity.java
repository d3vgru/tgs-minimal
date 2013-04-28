package org.theglobalsquare.framework.activity;

import java.io.File;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.theglobalsquare.app.R;
import org.theglobalsquare.app.config.EditPreferences;
import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityList;
import org.theglobalsquare.ui.AboutFragment;
import org.theglobalsquare.ui.CommunityListFragment;
import org.theglobalsquare.ui.FilesListFragment;
import org.theglobalsquare.ui.MonitorFragment;
//import org.theglobalsquare.ui.OverviewListFragment;
import org.theglobalsquare.ui.SearchFragment;
import org.theglobalsquare.ui.ViewCommunityFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


public abstract class TGSDrawerActivity extends TGSBaseActivity implements OnClickListener {
	public final static String TAG = "TGSDrawer";
	
	public final static String VIEW_COMMUNITY = "view community";
	
	private static final String STATE_MENUDRAWER = "org.theglobalsquare.framework.activity.TGSDrawerActivity.menuDrawer";
    private static final String STATE_ACTIVE_VIEW_ID = "org.theglobalsquare.framework.activity.TGSDrawerActivity.activeViewId";
    
    private MenuDrawer mMenuDrawer;
    private int mActiveViewId;
    
    protected TGSCommunityList mCommunities = new TGSCommunityList();

	private int mSelectedDrawer;
	
	private boolean m480Plus;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		m480Plus = metrics.widthPixels >= 480;
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_CONTENT, Position.LEFT, m480Plus);
		super.onCreate(savedInstanceState);
	}

	protected void configureDrawer(Bundle inState) {
		if (inState != null) {
	        mActiveViewId = inState.getInt(STATE_ACTIVE_VIEW_ID);
	    }

		setContentView(R.layout.main_activity);

        mMenuDrawer.setMenuView(R.layout.menu_scrollview);
        mMenuDrawer.setOffsetMenuEnabled(false);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        updateActiveDrawer();

        setSelectedDrawer(getTGSFacade().getDefaultDrawer());
        
        addClickListener(R.id.searchDrawer);
        addClickListener(R.id.fileDrawer);
        addClickListener(R.id.overviewDrawer);
        addClickListener(R.id.settingsDrawer);
        addClickListener(R.id.aboutDrawer);
        addClickListener(R.id.helpDrawer);
        addClickListener(R.id.monitorDrawer);
	}

	// safely add click listeners to menu drawer
	private void addClickListener(int vid) {
		View v = findViewById(vid);
		if(v == null)
			return;
		v.setOnClickListener(this);
	}
	
	// show proper fragment after clicking one of the items
	@Override
	public void onClick(View v) {
		android.util.Log.i(TAG, "click: " + v.getId());
		int drawer = getDrawerForViewId(v.getId());
		if(drawer < 0)
			return;
		setSelectedDrawer(drawer);
			
	}

	private void updateActiveDrawer() {
		if(mActiveViewId == -1) {
			return;
		}
		View activeView = findViewById(mActiveViewId);
        if (activeView != null) {
            mMenuDrawer.setActiveView(activeView);
        }
	}
	
	public int getDrawerForViewId(int vid) {
		int drawer = -1;
		switch(vid) {
			case R.id.searchDrawer:
				drawer = DRAWER_SEARCH;
				break;
			case R.id.fileDrawer:
				drawer = DRAWER_FILES;
				break;
			case R.id.overviewDrawer:
				drawer = DRAWER_OVERVIEW;
				break;
			case DRAWER_COMMUNITY:
				// TODO figure out what to actually do here
				drawer = -2;
				break;
			case R.id.settingsDrawer:
				drawer = DRAWER_SETTINGS;
				break;
			case R.id.helpDrawer:
				drawer = DRAWER_HELP;
				break;
			case R.id.aboutDrawer:
				drawer = DRAWER_ABOUT;
				break;
			case R.id.monitorDrawer:
				drawer = DRAWER_MONITOR;
				break;
			default:
				break;
		}
		return drawer;
	}
	
	public int updateViewIdForDrawer(int drawer, boolean showFragment) {
		int adjustedDrawer = drawer;
		int newActiveView = -1;
		if(DRAWER_SEARCH != drawer) {
			dismissKeyboardFor(this, (EditText)findViewById(R.id.txt_search_terms));
		}
		switch(drawer) {
			case DRAWER_SEARCH:
				newActiveView = R.id.searchDrawer;
				if(showFragment)
					showSearch();
				break;
			case DRAWER_FILES:
				newActiveView = R.id.fileDrawer;
				if(showFragment)
					showFiles();
				break;
			case DRAWER_OVERVIEW:
				// TODO eventually point to the actual community button
				if(showFragment)
					showOverview();
			case DRAWER_COMMUNITY:
				adjustedDrawer = DRAWER_OVERVIEW;
				newActiveView = R.id.overviewDrawer;
				break;
			case DRAWER_SETTINGS:
				// TODO maybe attach drawer to prefs activity
				// don't track this since it starts a new activity entirely
				adjustedDrawer = getSelectedDrawer();
				//newActiveView = R.id.settingsDrawer;
				// show settings
				if(showFragment) {
					Intent prefsIntent = new Intent();
					prefsIntent.setClass(this, EditPreferences.class);
					startActivityForResult(prefsIntent, PREFERENCES);
				}
				break;
			case DRAWER_HELP:
				newActiveView = R.id.helpDrawer;
				if(showFragment)
					showHelp();
				break;
			case DRAWER_ABOUT:
				newActiveView = R.id.aboutDrawer;
				if(showFragment)
					showAbout();
				break;
			case DRAWER_MONITOR:
				newActiveView = R.id.monitorDrawer;
				if(showFragment)
					showMonitor();
				break;
			default:
				break;
		}
		mActiveViewId = newActiveView;
		if(newActiveView > -1)
			updateActiveDrawer();
		closeDrawer();
		return adjustedDrawer;
	}
	
	public int getSelectedDrawer() {
		return mSelectedDrawer;
	}
		
	public void setSelectedDrawer(int drawer) {
		setSelectedDrawer(drawer, true);
	}
	public void setSelectedDrawer(int drawer, boolean showFragment) {
		setSelectedDrawer(drawer, true, null);
	}
	public void setSelectedDrawer(int drawer, boolean showFragment, String communityId) {
		mSelectedDrawer = drawer;
		
		// update menu drawer to show selected item
		int adjustedDrawer = updateViewIdForDrawer(drawer, showFragment);
		getTGSFacade().setDefaultDrawer(adjustedDrawer);
		if(drawer == DRAWER_COMMUNITY) {
			// TODO show the community
			
		}
	}
	
	private void showOverview() {
		// clear back stack
		clearBackStack(getSupportFragmentManager());
		/*
		FragmentTransaction ft = beginBackStackTransaction(true);
		OverviewListFragment ol = new OverviewListFragment();
		ft.add(R.id.mainContentFragment, ol);
		ft.commit();
		*/
	}
	
	private void showSearch() {
		// clear back stack
		FragmentTransaction ft = beginBackStackTransaction(true);
		SearchFragment s = new SearchFragment();
		ft.add(R.id.mainContentFragment, s);
		ft.commit();
	}
	
	private void showFiles() {
		// clear back stack
		FragmentTransaction ft = beginBackStackTransaction(true);
		FilesListFragment fl = new FilesListFragment();
		ft.add(R.id.mainContentFragment, fl);
		ft.commit();
	}
	
	private void showHelp() {
		/* TODO eventually..
		// dialog -- preserve back stack
		FragmentTransaction ft = beginBackStackTransaction();
		OverviewListFragment ol = new OverviewListFragment();
		ft.add(R.id.mainContentFragment, ol);
		ft.commit();
		*/
		//Toast.makeText(this, R.string.helpBtnLabel, Toast.LENGTH_SHORT).show();
		
		// for now, list the files in private storage
		// how helpful is that?
		monitorHomeDir();
		//setSelectedDrawer(DRAWER_HELP);
	}
	
	private void showAbout() {
		// dialog -- preserve back stack
		FragmentTransaction ft = beginBackStackTransaction();
		AboutFragment a = new AboutFragment();
		ft.add(R.id.mainContentFragment, a);
		ft.commit();
	}
	
	private void showMonitor() {
		// clear back stack
		FragmentTransaction ft = beginBackStackTransaction(true);
		MonitorFragment m = new MonitorFragment();
		ft.add(R.id.mainContentFragment, m);
		ft.commit();
	}
	
	@Override
    public void setContentView(int layoutResID) {
        // This override is only needed when using MENU_DRAG_CONTENT.
        mMenuDrawer.setContentView(layoutResID);
        onContentChanged();
    }
	
	@Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        mMenuDrawer.restoreState(inState.getParcelable(STATE_MENUDRAWER));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MENUDRAWER, mMenuDrawer.saveState());
        outState.putInt(STATE_ACTIVE_VIEW_ID, mActiveViewId);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	if(!m480Plus) {
            		mMenuDrawer.toggleMenu();
                	return true;
            	}
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
    	closeDrawer();
        super.onBackPressed();
    }
    
    protected void closeDrawer() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if(!m480Plus && (drawerState == MenuDrawer.STATE_OPEN
                || drawerState == MenuDrawer.STATE_OPENING)) {
            mMenuDrawer.closeMenu();
            return;
        }
    }
    
    protected void buildCommunityDrawers() {
		
	}

	public void addCommunity(TGSCommunity c) {
		// if tab name is blank, let's wait for the update event because screw trying to update later
		if(c == null || c.getName() == null || c.getName().equals(""))
			return;
		
		// add new community to set of tabs for squares
		mCommunities.add(c);
		
		// TODO add to drawer nav
		
		
		// show the new community
		showCommunity(getSupportFragmentManager(), c);
	}
	
	public static void showCommunityFromList(CommunityListFragment parent, int position) {
		TGSCommunity c = (TGSCommunity)parent.getListAdapter().getItem(position);
		showCommunity(parent.getFragmentManager(), c);
	}
	
	public static void showCommunity(FragmentManager fragMgr, TGSCommunity c) {
		// open a ViewCommunityFragment for the selected community
		ViewCommunityFragment viewCommunity = new ViewCommunityFragment();
		Bundle args = new Bundle();
		args.putSerializable(ViewCommunityFragment.COMMUNITY, c);
		viewCommunity.setArguments(args);
		
		FragmentTransaction ft = TGSBaseActivity.beginBackStackTransaction(fragMgr);
		ft.add(R.id.mainContentFragment, viewCommunity, VIEW_COMMUNITY);
		ft.commit();
	}

	public void monitorHomeDir() {
		File path = new File(getFilesDir().getAbsolutePath() + "/");
		if(!path.exists())
			return;
		monitor("listing files/(D)irectories in " + path.getAbsolutePath());
		File[] files = path.listFiles();
		if(files != null) {
			for(int i=0; i<files.length; i++) {
				File f = files[i];
				monitor(
						(f.isDirectory() ? "(D) " : "")
							+ f.getPath()
				);
			}
		}
	}

}
