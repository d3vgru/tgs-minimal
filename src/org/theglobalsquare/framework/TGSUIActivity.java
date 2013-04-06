package org.theglobalsquare.framework;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnKeyListener;
import android.widget.*;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.theglobalsquare.app.*;
import org.theglobalsquare.app.config.*;

// this class sets up and manages the main UI
public abstract class TGSUIActivity extends TGSTabActivity implements OnKeyListener {
	public final static String TAG = "TGSUI";
	
	public final static int PREFERENCES = 1001;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// load full layout
		ViewGroup mainLayout = (ViewGroup)findViewById(R.id.mainLayout);
		getLayoutInflater().inflate(R.layout.main_activity, mainLayout);
		
		monitor(TAG + ": INIT");
		
		// set the window title
        setTitle(getString(R.string.short_name));
        
        // attach click handlers
        configureButtons();

        // setup the default tabs
        configureTabs();
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    boolean visible = showActionButtons(mSelectedTab);
		mMenuCompose = menu.findItem(R.id.menu_compose);
		mMenuCompose.setVisible(visible);
		mMenuRefresh = menu.findItem(R.id.menu_refresh);
		mMenuRefresh.setVisible(visible);
		mMenuShare = menu.findItem(R.id.menu_share);
		mMenuShare.setVisible(visible);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// FIXME disable until startup complete
		switch(item.getItemId()) {
			case R.id.menu_compose:
				if(mComposerShowing)
					hideComposer();
				else showComposer();
				break;
			case R.id.menu_refresh:
				mTabsAdapter.notifyDataSetChanged();
				break;
			case R.id.menu_share:
				// TODO share action
				Toast.makeText(this, R.string.shareBtnLabel, Toast.LENGTH_SHORT).show();
				break;
			case R.id.menu_create:
				if(mSelectedTab == TAB_SEARCH) {
					// show search terms
					((EditText)findViewById(R.id.txt_search_terms)).setText(null);
					findViewById(R.id.group_search_terms).setVisibility(View.VISIBLE);
				} else {
					// TODO show new square dialog
					Toast.makeText(this, R.string.createBtnLabel, Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.menu_search:
				// select Search tab
		        setTab(TAB_SEARCH);
				break;
			case R.id.menu_settings:
				Intent prefsIntent = new Intent();
				prefsIntent.setClass(this, EditPreferences.class);
				startActivityForResult(prefsIntent, PREFERENCES);
				break;
			case R.id.menu_help:
				// TODO show help dialog
				Toast.makeText(this, R.string.helpBtnLabel, Toast.LENGTH_SHORT).show();
				break;
			case R.id.menu_about:
				// TODO show about dialog
				Toast.makeText(this, R.string.aboutBtnLabel, Toast.LENGTH_SHORT).show();
				break;
			default:
				// unknown option, maybe a superclass can handle it
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	
	
	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		switch(request) {
			case PREFERENCES:
				freshenConfig();
				break;
			default:
				break;
		}
		super.onActivityResult(request, result, data);
	}
	
	@Override
	public void onBackPressed() {
		if(mComposerShowing)
			hideComposer();
		else super.onBackPressed();
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		// http://stackoverflow.com/questions/1489852/android-handle-enter-in-an-edittext
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
        		(keyCode == KeyEvent.KEYCODE_ENTER)) {
        	// Perform action on key press
        	if(view instanceof EditText) {
        		// make sure this is the search terms field
        		if(view.getId() == R.id.txt_search_terms) {
        			submitSearch((EditText)view, sSearchFragment);
        			return true;
        		}
        	}
        }
    	// http://stackoverflow.com/questions/13179620/force-overflow-menu-in-actionbarsherlock
    	// Also need to update com.actionbarsherlock.internal.view.menu.ActionMenuPresenter
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	        if (event.getAction() == KeyEvent.ACTION_UP &&
	            keyCode == KeyEvent.KEYCODE_MENU) {
	            openOptionsMenu();
	            return true;
	        }
	    }
		return false;
	}
	
}
