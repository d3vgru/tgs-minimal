package org.theglobalsquare.framework;

import org.theglobalsquare.app.*;
import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.values.TGSConfig;

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
			// FIXME get a reference to the preference Honeycomb style
			return;
		} else {
			aliasPref = prefScreen.findPreference(prefKey);
		}
		if(aliasPref == null)
			return;
    	aliasPref.setSummary(summary);
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
		Facade f = getFacade();
    	TGSConfig c = f.getConfig();
	    if (key.equals(Facade.PREF_ALIAS)) {
	    	c.setName(f.getAlias());
			updateAlias();
	    } else if(key.equals(Facade.PREF_ENABLE_PROXY)) {
	    	c.setProxyEnabled(f.isProxyEnabled());
	    } else if(key.equals(Facade.PREF_REQUIRE_PROXY)) {
	    	c.setProxyRequired(f.isProxyRequired());
	    } else if(key.equals(Facade.PREF_PROXY_HOST)) {
	    	c.setProxyHost(f.getProxyHost());
	    	updateProxyHost();
	    } else if(key.equals(Facade.PREF_PROXY_PORT)) {
	    	c.setProxyPort(Integer.valueOf(f.getProxyPort()));
	    	updateProxyPort();
	    }
	    /* this does not do what you think it does
    	TGSConfigEvent changeEvent = TGSConfigEvent.forParamUpdated(c);
    	boolean toPy = true; // true to route to python
    	if(!TGSMainActivity.sendEvent(changeEvent, toPy))
    		android.util.Log.w(PreferenceActivity.TAG, "qToPy.add rejected");
    	else android.util.Log.i(PreferenceActivity.TAG, "event sent (toPy: " + toPy + ")");
    	*/
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
