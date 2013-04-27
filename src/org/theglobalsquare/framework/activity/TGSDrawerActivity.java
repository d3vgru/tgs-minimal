package org.theglobalsquare.framework.activity;

import net.simonvt.menudrawer.MenuDrawer;

import org.theglobalsquare.framework.values.TGSCommunity;
import org.theglobalsquare.framework.values.TGSCommunityList;
import org.theglobalsquare.ui.ViewCommunityFragment;

import com.actionbarsherlock.view.Window;


public abstract class TGSDrawerActivity extends TGSBaseActivity {
	public final static String TAG = "TGSDrawer";
	
	private static final String STATE_MENUDRAWER = "org.theglobalsquare.framework.activity.TGSDrawerActivity.menuDrawer";
    private static final String STATE_ACTIVE_VIEW_ID = "org.theglobalsquare.framework.activity.TGSDrawerActivity.activeViewId";
    
    private MenuDrawer mMenuDrawer;
    private int mActiveViewId;
    
    protected TGSCommunityList mCommunities = new TGSCommunityList();

	protected int mSelectedDrawer;
	
	protected void configureDrawer() {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		
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
		ViewCommunityFragment.show(getSupportFragmentManager(), c);
	}
	
	public void setDrawer(int drawer) {
		mSelectedDrawer = drawer;
		// TODO anything else
	}

}
