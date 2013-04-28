package org.theglobalsquare.framework.activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityList;
import org.theglobalsquare.ui.CommunityListFragment;
import org.theglobalsquare.ui.ViewCommunityFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;


public abstract class TGSDrawerActivity extends TGSFragmentSupport implements OnClickListener {
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
        
        updateActiveItem();

        setSelectedDrawer(getTGSFacade().getDefaultDrawer());
        
        addClickListener(R.id.searchDrawer);
        addClickListener(R.id.fileDrawer);
        addClickListener(R.id.overviewDrawer);
        addClickListener(R.id.settingsDrawer);
        addClickListener(R.id.aboutDrawer);
        addClickListener(R.id.helpDrawer);
        addClickListener(R.id.monitorDrawer);
        
	}
	
	void setActiveViewId(int activeViewId) {
		mActiveViewId = activeViewId;
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
		int drawer = DrawerViewMap.getDrawerForViewId(v.getId());
		if(drawer < 0)
			return;
		setSelectedDrawer(drawer);
	}
	
	@Override
	void updateActiveItem() {
		if(mActiveViewId == -1) {
			return;
		}
		View activeView = findViewById(mActiveViewId);
        if (activeView != null) {
            mMenuDrawer.setActiveView(activeView);
        }
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
		// update menu drawer to show selected item
		mSelectedDrawer = DrawerViewMap.getAdjustedDrawer(this, drawer, showFragment);
		if(drawer == DRAWER_COMMUNITY) {
			// TODO show the community
			
		}
		getTGSFacade().setDefaultDrawer(mSelectedDrawer);
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
    	if(!closeDrawer()) {
    		setStayOpen(false);
        	super.onBackPressed();
    	}
    }
    
    boolean closeDrawer() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if(!m480Plus && (drawerState == MenuDrawer.STATE_OPEN
                || drawerState == MenuDrawer.STATE_OPENING)) {
            mMenuDrawer.closeMenu();
        	// only return true if we actually close the drawer
            return true;
        }
        return false;
    }
    
	public void addCommunity(TGSCommunity c) {
		// if tab name is blank, let's wait for the update event because screw trying to update later
		if(c == null || c.getName() == null || c.getName().equals(""))
			return;
		
		// add new community to set of tabs for squares
		mCommunities.add(c);
		
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
		
		// don't clear the backstack since this is on top of the overview
		FragmentTransaction ft = TGSFragmentSupport.beginBackStackTransaction(fragMgr, false);
		ft.add(R.id.mainContentFragment, viewCommunity, VIEW_COMMUNITY);
		ft.commit();
	}

	void showItem(Fragment f, String tag) {
		showItem(f, tag, true);
	}
	void showItem(Fragment f, String tag, boolean clearBackStack) {
		setStayOpen(true);
		FragmentTransaction ft = beginBackStackTransaction(clearBackStack, tag);
		ft.add(R.id.mainContentFragment, f, tag); // do we want/need to set tag here?
		ft.commit();
	}
	

}
