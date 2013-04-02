package org.theglobalsquare.framework;

import org.theglobalsquare.app.R;

import android.content.*;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;

public class PreferenceActivity extends CompatiblePreferenceActivity
		implements OnSharedPreferenceChangeListener {
	public final static String ALIAS = "alias";
	public final static String REQUIRE_TOR = "require_tor";

	// FIXME this doesn't actually do anything
	private void updateAlias() {
		PreferenceScreen prefScreen = getPreferenceScreen();
		if(prefScreen == null)
			return;
		Preference aliasPref = prefScreen.findPreference(ALIAS);
		if(aliasPref == null)
			return;
		SharedPreferences sharedPreferences = prefScreen.getSharedPreferences();
    	aliasPref.setSummary(sharedPreferences.getString(ALIAS, ""));
	}

	// http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    if (key.equals(ALIAS)) {
			updateAlias();
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setPrefs(R.xml.settings);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		PreferenceScreen prefScreen = getPreferenceScreen();
		if(prefScreen == null)
			return;
		updateAlias();
		prefScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		PreferenceScreen prefScreen = getPreferenceScreen();
		if(prefScreen == null)
			return;
		prefScreen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

}
