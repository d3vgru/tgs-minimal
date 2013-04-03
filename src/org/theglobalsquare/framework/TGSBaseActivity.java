package org.theglobalsquare.framework;

import java.util.*;

import org.kivy.android.PythonActivity;
import org.theglobalsquare.app.Facade;
import org.theglobalsquare.app.R;
import org.theglobalsquare.ui.MonitorFragment;
import org.theglobalsquare.ui.OverviewListFragment;
import org.theglobalsquare.ui.SearchFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

// this class sets up and manages the tabs in the main_activity layout
// TODO move tab stuff into TGSTabActivity

// adapted from https://bitbucket.org/owentech/abstabsviewpager
// depends on ActionBarSherlock
public class TGSBaseActivity extends PythonActivity {
	public final static int TAB_COUNT_BASE = 3; // number of default tabs
	public final static int TAB_SEARCH = 0; // search tab
	public final static int TAB_OVERVIEW = 1; // my squares overview
	public final static int TAB_MONITOR = 2; // debug monitor
	
	protected int selectedTab = -1;
	
	protected MenuItem menuCompose = null;
	protected MenuItem menuRefresh = null;
	protected MenuItem menuShare = null;
	protected MenuItem menuCreate = null;
	
	protected ViewPager mViewPager;
    protected TabsAdapter mTabsAdapter;
    
	protected boolean showActionButtons(int tab) {
		return tab >= TAB_COUNT_BASE;
	}
	
	public Facade getFacade() {
		return (Facade)getApplication();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar bar = getSupportActionBar();        
        bar.setDisplayShowHomeEnabled(false);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	protected void configureTabs() {
		ActionBar bar = getSupportActionBar();
		
		// make sure to setContentView() first (in super.onCreate)
		mViewPager = (ViewPager)findViewById(R.id.mainActivityPager);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		// TAB_SEARCH
        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.searchBtnLabel),
                SearchFragment.class, null);
        // TAB_OVERVIEW
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
				selectedTab = tab;
				boolean visible = showActionButtons(tab);
				if(menuCompose != null)
					menuCompose.setVisible(visible);
				if(menuRefresh != null)
					menuRefresh.setVisible(visible);
				if(menuShare != null)
					menuShare.setVisible(visible);
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
			return Fragment.instantiate(mContext, info.clss.getName(),
                    info.args);
		}

		public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {
		}

		public void onPageSelected(int position)
		{
			mActionBar.setSelectedNavigationItem(position);
		}

		public void onPageScrollStateChanged(int state) {
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
			// NOOP
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// NOOP
		}
	}
	
}
