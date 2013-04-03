package org.theglobalsquare.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.*;

public class Facade extends Application  implements PropertyChangeListener {
	// main access point for python side to talk to Android application
	public final static String TAG = "Facade";
	
	public final static String PREF_ALIAS = "pref_alias";
	public final static String PREF_ENABLE_TOR = "pref_enable_tor";
	public final static String PREF_REQUIRE_TOR = "pref_require_tor";
	public final static String PREF_TOR_HOST = "pref_tor_host";
	public final static String PREF_TOR_PORT = "pref_tor_port";

	private TGSEventProxy events = new TGSEventProxy();
	
	public TGSEventProxy getEvents() {
		return events;
	}

	public boolean sendEvent(TGSEvent e) {
		if(events == null)
			return false;
		events.sendEvent(e);
		return true;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// make sure config events are propagating
		events.addListener(TGSConfigEvent.class, this);

		// get events from python (or else AndroidFacade in python won't be able to send us TGSSystemEvent)
		events.addListener(TGSSystemEvent.class, this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		android.util.Log.d(Facade.TAG, "propertyChange: " + event + ", new value: " + event.getNewValue());
		TGSMainActivity.handle(event.getNewValue());
	}

	private static TGSConfig config = null;
	
	public SharedPreferences getPrefs() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public TGSConfig getConfig() {
		if(config == null) {
			config = new TGSConfig();
			config.setName(getAlias());
			config.setProxyRequired(isRequireTor());
			config.setProxyEnabled(isEnableTor());
			config.setProxyHost(getProxyHost());
			config.setProxyPort(Integer.valueOf(getProxyPort()));
		}
		return config;
	}
	
	public String getAlias() {
		return getPrefs().getString(PREF_ALIAS, getResources().getString(R.string.anonLabel));
	}
	
	public boolean isEnableTor() {
		return getPrefs().getBoolean(PREF_ENABLE_TOR, false);
	}

	public boolean isRequireTor() {
		return getPrefs().getBoolean(PREF_REQUIRE_TOR, false);
	}
	
	public String getProxyHost() {
		return getPrefs().getString(PREF_TOR_HOST, getResources().getString(R.string.localhostLabel));
	}
	
	public String getProxyPort() {
		return getPrefs().getString(PREF_TOR_PORT, getResources().getString(R.string.proxyPortDefault));
	}
}
