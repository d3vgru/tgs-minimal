package org.theglobalsquare.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	
	private Map<Class<? extends TGSEvent>, Set<PropertyChangeListener>> listeners;

	private Queue<TGSEvent> qToPy;
	public int queueSize() {
		return qToPy.size();
	}

	/*
	private TGSEventProxy events = null;
	public void setEvents(TGSEventProxy events) {
		android.util.Log.w(TGSEventProxy.TAG, "NEW EVENT PROXY");

		// get events from python (or else AndroidFacade in python won't be able to send us TGSSystemEvent)
		events.addListener(TGSSystemEvent.class, this);		

		this.events = events;
	}
	public TGSEventProxy getEvents() {
		if(events == null) {
			android.util.Log.w(TGSEventProxy.TAG, "EVENTS NULL");
		}
		return events;
	}
	*/
	
	public TGSEvent nextEvent() {
		return qToPy.poll();
	}

	public boolean sendEvent(TGSEvent e) {
		return sendEvent(e, false);
	}
	public boolean sendEvent(TGSEvent e, boolean toPy) {
		if(toPy)
			return qToPy.add(e);
		for(Class<? extends TGSEvent> c : this.listeners.keySet()) {
			if(c == null // want to know all events
					|| e.getClass().isAssignableFrom(c)) { // e instanceof c
				Set<PropertyChangeListener> ls = this.listeners.get(c);
				for(PropertyChangeListener l : ls)
					l.propertyChange(new PropertyChangeEvent(this, "qFromPy", null, e));
			}
		}
		return true;
	}

	@Override
	public void onCreate() {
		listeners = new HashMap<Class<? extends TGSEvent>, Set<PropertyChangeListener>>();
		qToPy = new ConcurrentLinkedQueue<TGSEvent>();
		super.onCreate();
	}
	
	public void addListener(Class<? extends TGSEvent> c, PropertyChangeListener l) {
		Set<PropertyChangeListener> ls = this.listeners.get(c);
		if(ls == null) {
			ls = new HashSet<PropertyChangeListener>();
			this.listeners.put(c, ls);
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
