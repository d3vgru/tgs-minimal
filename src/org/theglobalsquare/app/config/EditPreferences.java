package org.theglobalsquare.app.config;

import net.saik0.android.unifiedpreference.UnifiedPreferenceFragment;
import net.saik0.android.unifiedpreference.UnifiedSherlockPreferenceActivity;

import org.theglobalsquare.app.*;

import org.theglobalsquare.framework.ITGSFacade;

import android.annotation.SuppressLint;
import android.content.*;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import android.os.Build;
import android.os.Bundle;
import android.preference.*;

// while this activity has focus, we don't have access to the rest of the app
// (Android makes a new context)
// fairly certain events from python won't be handled properly while this activity has focus
public class EditPreferences extends UnifiedSherlockPreferenceActivity
		implements OnSharedPreferenceChangeListener {
	public final static String TAG = "EditPreferences";
	public final static String SHARED_PREFS_KEY = "tgs_shared_prefs";

	public ITGSFacade getTGSFacade() {
		return (ITGSFacade)getApplication();
	}

	private void updatePref(String prefKey, String summary) {
		// for 2.3 support (3.0+ uses %s in summary string)
		Preference pref = findPreference(prefKey);
		if(pref == null)
			return;
    	pref.setSummary(summary);
	}

	private void updateAlias() {
		updatePref(Facade.PREF_ALIAS, getTGSFacade().getAlias());
	}

	private void updateProxyHost() {
		updatePref(Facade.PREF_PROXY_HOST, getTGSFacade().getProxyHost());
	}

	private void updateProxyPort() {
		updatePref(Facade.PREF_PROXY_PORT, getTGSFacade().getProxyPort());
	}

	// http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	android.util.Log.d(TAG, "prefs changed: " + key);
	    if (key.equals(Facade.PREF_ALIAS)) {
			updateAlias();
	    } else if(key.equals(Facade.PREF_PROXY_HOST)) {
	    	updateProxyHost();
	    } else if(key.equals(Facade.PREF_PROXY_PORT)) {
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
		getTGSFacade().getPrefs().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getTGSFacade().getPrefs().unregisterOnSharedPreferenceChangeListener(this);
	}

	public static class Fragment extends UnifiedPreferenceFragment {}
}
