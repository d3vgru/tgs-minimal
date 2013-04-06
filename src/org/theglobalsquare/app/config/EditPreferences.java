package org.theglobalsquare.app.config;

import net.saik0.android.unifiedpreference.UnifiedPreferenceFragment;
import net.saik0.android.unifiedpreference.UnifiedSherlockPreferenceActivity;

import org.theglobalsquare.app.*;

//import org.theglobalsquare.framework.values.TGSConfig;

import android.annotation.SuppressLint;
import android.content.*;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import android.os.Build;
import android.os.Bundle;
import android.preference.*;

public class EditPreferences extends UnifiedSherlockPreferenceActivity
		implements OnSharedPreferenceChangeListener {
	public final static String TAG = "PreferenceActivity";
	public final static String SHARED_PREFS_KEY = "tgs_shared_prefs";

	public Facade getFacade() {
		return (Facade)getApplication();
	}

	private void updatePref(String prefKey, String summary) {
		// for 2.3 support (3.0+ uses %s in summary string)
		Preference pref = findPreference(prefKey);
		if(pref == null)
			return;
    	pref.setSummary(summary);
	}

	private void updateAlias() {
		updatePref(Facade.PREF_ALIAS, getFacade().getAlias());
	}

	private void updateProxyHost() {
		updatePref(Facade.PREF_PROXY_HOST, getFacade().getProxyHost());
	}

	private void updateProxyPort() {
		updatePref(Facade.PREF_PROXY_PORT, getFacade().getProxyPort());
	}

	// http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	android.util.Log.i(TAG, "prefs changed: " + key);
//		Facade f = getFacade();
//    	TGSConfig c = f.getConfig();
	    if (key.equals(Facade.PREF_ALIAS)) {
//	    	c.setName(f.getAlias());
			updateAlias();
			/*
	    } else if(key.equals(Facade.PREF_ENABLE_PROXY)) {
	    	c.setProxyEnabled(f.isProxyEnabled());
	    } else if(key.equals(Facade.PREF_REQUIRE_PROXY)) {
	    	c.setProxyRequired(f.isProxyRequired());
	    	*/
	    } else if(key.equals(Facade.PREF_PROXY_HOST)) {
//	    	c.setProxyHost(f.getProxyHost());
	    	updateProxyHost();
	    } else if(key.equals(Facade.PREF_PROXY_PORT)) {
//	    	c.setProxyPort(Integer.valueOf(f.getProxyPort()));
	    	updateProxyPort();
	    }
	}
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Set header resource MUST BE CALLED BEFORE super.onCreate 
		setHeaderRes(R.xml.preference_headers);
		
		// set desired preference file and mode
		setSharedPreferencesName(SHARED_PREFS_KEY);
		
		// multi process or changes won't be reflected immediately
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			setSharedPreferencesMode(Context.MODE_MULTI_PROCESS);
		else setSharedPreferencesMode(Context.MODE_PRIVATE);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateAlias();
		updateProxyHost();
		updateProxyPort();
		getFacade().getPrefs().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getFacade().getPrefs().unregisterOnSharedPreferenceChangeListener(this);
	}

	public static class Fragment extends UnifiedPreferenceFragment {}
}
