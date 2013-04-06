package org.theglobalsquare.framework;

import org.kivy.android.PythonActivity;
import org.theglobalsquare.app.Facade;
import org.theglobalsquare.framework.values.TGSConfig;
import org.theglobalsquare.framework.values.TGSConfigEvent;

import android.os.Bundle;
import android.support.v4.view.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

// this class sets up and manages the tabs in the main_activity layout

// adapted from https://bitbucket.org/owentech/abstabsviewpager
// depends on ActionBarSherlock
public abstract class TGSBaseActivity extends PythonActivity implements ITGSActivity {	
	protected MenuItem menuCompose = null;
	protected MenuItem menuRefresh = null;
	protected MenuItem menuShare = null;
	protected MenuItem menuCreate = null;
	
	protected ViewPager mViewPager;
    
	public Facade getFacade() {
		return (Facade)getApplication();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar bar = getSupportActionBar();        
        bar.setDisplayShowHomeEnabled(false);
	}
			
	public void freshenConfig() {
		TGSConfig config = getFacade().getConfig();
		TGSConfigEvent e = TGSConfigEvent.forParamUpdated(config);
		Facade.sendEvent(e, true);
	}

}
