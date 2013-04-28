package org.theglobalsquare.framework.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.View;
import android.widget.TextView;

public abstract class TGSFragmentSupport extends TGSBaseActivity {
	public final static String TAG = "FragSup";

    private boolean mStayOpen = true;
    
    void setStayOpen(boolean stayOpen) {
    	mStayOpen = stayOpen;
    }
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	// manage selected tab using backstack listener, tagging each transaction with the view id
        final FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				// if the tag of the transaction matches an id for a menu drawer button..
				int count = fm.getBackStackEntryCount();
				if(count == 0) {
					if(!mStayOpen) {
						android.util.Log.i(TAG, "backStack empty, exiting");
						finish();
					}
					return;
				}
				FragmentManager.BackStackEntry topEntry = fm.getBackStackEntryAt(count - 1);
				String tag = topEntry.getName();
				if(tag != null) {
					String pkg = getPackageName();
					int idFromTag = getResources().getIdentifier(tag + "Drawer", "id", pkg);
					if(idFromTag == 0)
						return;
					String menuTitle = getMenuTitle(idFromTag);
					if(!"".equals(menuTitle)) {
						setActiveViewId(idFromTag);
						updateActiveItem();
						setTitle(menuTitle);
					}
				} else {
					android.util.Log.w(TAG, "got backStack but tag was NULL");
				}
			}
        });
	}

	String getMenuTitle(int menuId) {
		// menuId is the android:id of the menu drawer item
		View v = findViewById(menuId);
		if(v == null)
			return "";
		if(v instanceof TextView) {
			CharSequence cs = ((TextView)v).getText();
			if(cs != null)
				return cs.toString();
		}
		return "";
	}

	public FragmentTransaction beginBackStackTransaction() {
		return beginBackStackTransaction(getSupportFragmentManager());
	}

	public FragmentTransaction beginBackStackTransaction(boolean clearBackStack) {
		return beginBackStackTransaction(getSupportFragmentManager(), clearBackStack);
	}

	public FragmentTransaction beginBackStackTransaction(boolean clearBackStack, String name) {
		return beginBackStackTransaction(getSupportFragmentManager(), clearBackStack, name);
	}

	public static FragmentTransaction beginBackStackTransaction(FragmentManager fm) {
		return beginBackStackTransaction(fm, false);
	}
	
	public static FragmentTransaction beginBackStackTransaction(FragmentManager fm,
			boolean clearBackStack) {
		return beginBackStackTransaction(fm, clearBackStack, null);
	}
	
	@SuppressLint("CommitTransaction")
	public static FragmentTransaction beginBackStackTransaction(FragmentManager fm,
			boolean clearBackStack, String name) {
		if(clearBackStack) {
			clearBackStack(fm);
		}
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(name);
		return ft;
	}
	
	protected static void clearBackStack(FragmentManager fm) {
		for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
		    fm.popBackStack();
		}
	}
	
	abstract void setActiveViewId(int vid);
    abstract void updateActiveItem();
    abstract void showItem(Fragment f, String tag);
    abstract void showItem(Fragment f, String tag, boolean clearBackStack);
}
