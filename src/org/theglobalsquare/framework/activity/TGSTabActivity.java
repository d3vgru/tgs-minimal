package org.theglobalsquare.framework.activity;

import java.util.ArrayList;

import org.theglobalsquare.app.R;
import org.theglobalsquare.ui.SearchFragment;
import org.theglobalsquare.ui.OverviewListFragment;
import org.theglobalsquare.ui.FilesListFragment;
import org.theglobalsquare.ui.MonitorFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;

public abstract class TGSTabActivity extends TGSBaseActivity {
	public final static int TAB_COUNT_BASE = 4; // number of default tabs
	public final static int TAB_SEARCH = 0; // search tab
	public final static int TAB_OVERVIEW = 1; // my squares overview
	public final static int TAB_FILES = 2; // files tab
	public final static int TAB_MONITOR = 3; // debug monitor
	
	protected int mSelectedTab = -1;
    protected TabsAdapter mTabsAdapter;
	protected ViewPager mViewPager;

    protected boolean showActionButtons(int tab) {
		return tab >= TAB_COUNT_BASE;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);    	
    }
	
	protected void configureTabs() {
		ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// make sure to setContentView() first (in super.onCreate)
		mViewPager = (ViewPager)findViewById(R.id.mainActivityPager);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		// TAB_SEARCH
        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.searchBtnLabel),
                SearchFragment.class, null);
        // TAB_OVERVIEW
        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.filesLabel),
                FilesListFragment.class, null);
        // TAB_FILES
        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.overviewLabel),
                OverviewListFragment.class, null);
        // TAB_MONITOR
        mTabsAdapter.addTab(
        		bar.newTab().setText(R.string.monitorLabel),
        		MonitorFragment.class, null);
        
        // listener for tab change
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// NOOP
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// NOOP
			}

			@Override
			public void onPageSelected(int tab) {
				// hide action buttons if not viewing a square
				mSelectedTab = tab;
				boolean visible = showActionButtons(tab);
				if(mMenuCompose != null)
					mMenuCompose.setVisible(visible);
				if(mMenuRefresh != null)
					mMenuRefresh.setVisible(visible);
				if(mMenuShare != null)
					mMenuShare.setVisible(visible);
				setTab(tab);
			}
        	
        });
        
        // select Monitor tab
        setTab(TAB_MONITOR);
	}
	
	public void setTab(int tab) {
		getSupportActionBar().setSelectedNavigationItem(tab);
	}

	public static class TabsAdapter extends FragmentStatePagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		
		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;
		
			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}
		
		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}
		
		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return mTabs.size();
		}
		
		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			Fragment f = Fragment.instantiate(mContext, info.clss.getName(),
		            info.args);
			if(f instanceof SearchFragment) {
				sSearchFragment = (SearchFragment)f;
			}
			return f;
		}
		
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// NOOP
		}
		
		public void onPageSelected(int position)
		{
			mActionBar.setSelectedNavigationItem(position);
    		if(position == TAB_SEARCH) {
    			android.util.Log.i(TAG, "tabs.onPageSelected: SEARCH");
    			// hackish, but...
    			// FIXME only do this if the user tapped the tab
    			showSearchTerms(mActivity);
    		}
		}
		
		public void onPageScrollStateChanged(int state) {
			// NOOP
		}
		
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
		            mViewPager.setCurrentItem(i);
		            break;
				}
			}
		}
		
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					if(i == TAB_SEARCH && mActivity != null) {
						EditText et = (EditText)mActivity.findViewById(R.id.txt_search_terms);
						if(et != null)
							dismissKeyboardFor(mActivity, et);
					}
				}
			}
		}
		
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					if(i == TAB_SEARCH && mActivity != null) {
						showSearchTerms(mActivity);
					}
				}
			}
		}
	}

}
