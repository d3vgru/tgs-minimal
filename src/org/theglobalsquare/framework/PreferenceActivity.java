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

	// FIXME this doesn't actually do anything
	private void updateAlias() {
    	android.util.Log.i(TAG, "updating alias");
		PreferenceScreen prefScreen = getPreferenceScreen();
		if(prefScreen == null)
			return;
		Preference aliasPref = prefScreen.findPreference(Facade.PREF_ALIAS);
		if(aliasPref == null)
			return;
    	aliasPref.setSummary(getFacade().getAlias());
	}

	// http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	android.util.Log.i(TAG, "prefs changed: " + key);
		Facade f = getFacade();
    	TGSConfig c = f.getConfig();
	    if (key.equals(Facade.PREF_ALIAS)) {
	    	android.util.Log.i(TAG, "new alias: " + f.getAlias());
	    	c.setName(f.getAlias());
			updateAlias();
	    } else if(key.equals(Facade.PREF_ENABLE_TOR)) {
	    	c.setProxyEnabled(f.isEnableTor());
	    } else if(key.equals(Facade.PREF_REQUIRE_TOR)) {
	    	c.setProxyRequired(f.isRequireTor());
	    } else if(key.equals(Facade.PREF_TOR_HOST)) {
	    	
	    }
    	TGSConfigEvent changeEvent = TGSConfigEvent.forParamUpdated(c);
    	f.getEvents().sendEvent(changeEvent, true);
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
		getFacade().getPrefs().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getFacade().getPrefs().unregisterOnSharedPreferenceChangeListener(this);
	}

}
