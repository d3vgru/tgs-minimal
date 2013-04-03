package org.theglobalsquare.framework;

import org.theglobalsquare.app.Facade;
import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.values.TGSConfig;
import org.theglobalsquare.framework.values.TGSConfigEvent;

import android.content.*;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;

public class PreferenceActivity extends CompatiblePreferenceActivity
		implements OnSharedPreferenceChangeListener {
	public final static String TAG = "PreferenceActivity";

	public Facade getFacade() {
		return (Facade)getApplication();
	}
	
	private void updatePref(String prefKey, String summary) {
		Preference aliasPref = null;
		PreferenceScreen prefScreen = getPreferenceScreen();
		if(prefScreen == null) {
			android.util.Log.i(PreferenceActivity.TAG, "no PreferenceScreen");
			// FIXME get a reference to the pref using Honeycomb style
			return;
		} else {
			aliasPref = prefScreen.findPreference(prefKey);
		}
		if(aliasPref == null)
			return;
    	aliasPref.setSummary(summary);
	}

	private void updateAlias() {
    	android.util.Log.i(TAG, "updating alias");
		updatePref(Facade.PREF_ALIAS, getFacade().getAlias());
	}

	private void updateProxyHost() {
    	android.util.Log.i(TAG, "updating proxy host");
		updatePref(Facade.PREF_TOR_HOST, getFacade().getProxyHost());
	}

	private void updateProxyPort() {
		updatePref(Facade.PREF_TOR_PORT, getFacade().getProxyPort());
	}

	// http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	android.util.Log.i(TAG, "prefs changed: " + key);
		Facade f = getFacade();
    	TGSConfig c = f.getConfig();
	    if (key.equals(Facade.PREF_ALIAS)) {
	    	c.setName(f.getAlias());
			updateAlias();
	    } else if(key.equals(Facade.PREF_ENABLE_TOR)) {
	    	c.setProxyEnabled(f.isEnableTor());
	    } else if(key.equals(Facade.PREF_REQUIRE_TOR)) {
	    	c.setProxyRequired(f.isRequireTor());
	    } else if(key.equals(Facade.PREF_TOR_HOST)) {
	    	c.setProxyHost(f.getProxyHost());
	    	updateProxyHost();
	    } else if(key.equals(Facade.PREF_TOR_PORT)) {
	    	c.setProxyPort(Integer.valueOf(f.getProxyPort()));
	    	updateProxyPort();
	    }
    	TGSConfigEvent changeEvent = TGSConfigEvent.forParamUpdated(c);
    	boolean toPy = true; // true to route to python
    	if(!f.getEvents().sendEvent(changeEvent, toPy))
    		android.util.Log.w(PreferenceActivity.TAG, "offer rejected");
    	android.util.Log.i(PreferenceActivity.TAG, "event sent (toPy: " + toPy + ")");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setPrefs(R.xml.settings);
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

}
