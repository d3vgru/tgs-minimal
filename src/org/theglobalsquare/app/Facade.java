package org.theglobalsquare.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.*;

public class Facade extends Application {
	// main access point for python side to talk to Android application
	public final static String TAG = "Facade";
	
	public final static String PREF_ALIAS = "pref_alias";
	public final static String PREF_ENABLE_PROXY = "pref_enable_proxy";
	public final static String PREF_REQUIRE_PROXY = "pref_require_proxy";
	public final static String PREF_PROXY_HOST = "pref_proxy_host";
	public final static String PREF_PROXY_PORT = "pref_proxy_port";
	
	private static Map<Class<? extends TGSEvent>, Set<PropertyChangeListener>> listeners = null;
	private static TGSEvent event = null;
	static TGSEvent pyEvent = null; 

	@Override
	public void onCreate() {
		if(listeners == null) {
			android.util.Log.d(Facade.TAG, "NEW LISTENERS");
			listeners = new HashMap<Class<? extends TGSEvent>, Set<PropertyChangeListener>>();
		}
		super.onCreate();
	}
	
	public static boolean sendEvent(TGSEvent e) {
		for(Class<? extends TGSEvent> c : listeners.keySet()) {
			if(c == null // want to know all events
					|| e.getClass().isAssignableFrom(c)) { // e instanceof c
				Set<PropertyChangeListener> ls = listeners.get(c);
				for(PropertyChangeListener l : ls)
					l.propertyChange(new PropertyChangeEvent(Facade.class, "qFromPy", null, e));
			}
		}
		Facade.event = e;
		return true;
	}
	public static TGSEvent getEvent() {
		return event;
	}
	public static void setEvent(TGSEvent e) {
		pyEvent = e;
	}
	public void addListener(Class<? extends TGSEvent> c, PropertyChangeListener l) {
		Set<PropertyChangeListener> ls = listeners.get(c);
		if(ls == null) {
			ls = new HashSet<PropertyChangeListener>();
			listeners.put(c, ls);
		}
		ls.add(l);
	}

	private static TGSConfig config = null;
	
	public SharedPreferences getPrefs() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public TGSConfig getConfig() {
		if(config == null) {
			config = new TGSConfig();
			config.setName(getAlias());
			config.setProxyRequired(isProxyRequired());
			config.setProxyEnabled(isProxyEnabled());
			config.setProxyHost(getProxyHost());
			config.setProxyPort(Integer.valueOf(getProxyPort()));
		}
		return config;
	}
	
	public String getAlias() {
		return getPrefs().getString(PREF_ALIAS, getResources().getString(R.string.anonLabel));
	}
	
	public boolean isProxyEnabled() {
		return getPrefs().getBoolean(PREF_ENABLE_PROXY, false);
	}

	public boolean isProxyRequired() {
		return getPrefs().getBoolean(PREF_REQUIRE_PROXY, false);
	}
	
	public String getProxyHost() {
		return getPrefs().getString(PREF_PROXY_HOST, getResources().getString(R.string.localhostLabel));
	}
	
	public String getProxyPort() {
		return getPrefs().getString(PREF_PROXY_PORT, getResources().getString(R.string.proxyPortDefault));
	}
}
