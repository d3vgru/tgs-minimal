package org.theglobalsquare.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.theglobalsquare.app.config.EditPreferences;
import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.*;

public class Facade extends Application implements ITGSFacade {
	// main access point for python side to talk to Android application (as opposed to activity)
	public final static String TAG = "Facade";
	
	private static Map<Class<? extends TGSEvent>, Set<PropertyChangeListener>> sListeners = null;
	
	@Override
	public void onCreate() {
		if(sListeners == null) {
			android.util.Log.d(TAG, "NEW LISTENERS");
			sListeners = new HashMap<Class<? extends TGSEvent>, Set<PropertyChangeListener>>();
		}

		if(qToPy == null) {
			android.util.Log.d(TAG, "NEW QUEUE");
			qToPy = new ConcurrentLinkedQueue<TGSEvent>();
		}

		super.onCreate();
	}
	
	private static Queue<TGSEvent> qToPy = null;
	
	public static int queueSize() {
		return qToPy.size();
	}

	public static TGSEvent nextEvent() {
		return qToPy.poll();
	}

	public static boolean sendEvent(TGSEvent e) {
		return sendEvent(e, false);
	}
	public static boolean sendEvent(TGSEvent e, boolean toPy) {
		if(toPy) {
			if(qToPy == null) {
				android.util.Log.d(TAG, "NEW QUEUE ON SEND EVENT");
				qToPy = new ConcurrentLinkedQueue<TGSEvent>();
			}
			boolean success = qToPy.add(e);
			return success;
		}
		for(Class<? extends TGSEvent> c : sListeners.keySet()) {
			if(c == null // want to know all events
					|| e.getClass().isAssignableFrom(c)) { // e instanceof c
				Set<PropertyChangeListener> ls = sListeners.get(c);
				for(PropertyChangeListener l : ls)
					l.propertyChange(new PropertyChangeEvent(Facade.class, "qFromPy", null, e));
			}
		}
		return true;
	}

	public void addListener(Class<? extends TGSEvent> c, PropertyChangeListener l) {
		Set<PropertyChangeListener> ls = sListeners.get(c);
		if(ls == null) {
			ls = new HashSet<PropertyChangeListener>();
			sListeners.put(c, ls);
		}
		ls.add(l);
	}
	public void removeListener(Class<? extends TGSEvent> c, PropertyChangeListener l) {
		Set<PropertyChangeListener> ls = sListeners.get(c);
		if(ls != null) {
			ls.remove(l);
		}
	}

	private static TGSConfig config = null;
	
	@SuppressLint("InlinedApi")
	public SharedPreferences getPrefs() {
		// multi process or changes won't be reflected immediately
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			return getSharedPreferences(EditPreferences.SHARED_PREFS_KEY, Context.MODE_MULTI_PROCESS);
		return getSharedPreferences(EditPreferences.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
	}
	
	public TGSConfig getConfig() {
		if(config == null) {
			config = new TGSConfig();
		}
		refreshConfig();
		return config;
	}
	
	private void refreshConfig() {
		String alias = getAlias();
		config.setName(alias);
		TGSUser.getMe().setName(alias);
		config.setDefaultDrawer(getDefaultDrawer());
		config.setDispersyEnabled(isDispersyEnabled());
		config.setDispersyPort(Integer.valueOf(getDispersyPort()));
		config.setProxyEnabled(isProxyEnabled());
		config.setProxyRequired(isProxyRequired());
		config.setProxyHost(getProxyHost());
		config.setProxyPort(Integer.valueOf(getProxyPort()));
		config.setSwiftEnabled(isSwiftEnabled());
		config.setTunnelDispersyOverSwift(isTunnelDispersyOverSwift());
	}
	
	public String getAlias() {
		return getPrefs().getString(PREF_ALIAS, getResources().getString(R.string.anonLabel));
	}
	
	public int getDefaultDrawer() {
		return getPrefs().getInt(PREF_DEFAULT_DRAWER, ITGSActivity.DRAWER_OVERVIEW);
	}
	
	public boolean isDispersyEnabled() {
		boolean dispersyEnabled = getPrefs().getBoolean(PREF_ENABLE_DISPERSY, true);
		android.util.Log.v(TAG, "dispersyEnabled: " + dispersyEnabled);
		return dispersyEnabled;
	}

	public String getDispersyPort() {
		return getPrefs().getString(PREF_DISPERSY_PORT, getResources().getString(R.string.dispersyPortDefault));
	}

	public boolean isProxyEnabled() {
		boolean proxyEnabled = getPrefs().getBoolean(PREF_ENABLE_PROXY, false);
		android.util.Log.v(TAG, "proxyEnabled: " + proxyEnabled);
		return proxyEnabled;
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
	
	public boolean isSwiftEnabled() {
		return getPrefs().getBoolean(PREF_SWIFT_ENABLED, false);
	}

	public boolean isTunnelDispersyOverSwift() {
		return getPrefs().getBoolean(PREF_TUNNEL_DISPERSY_OVER_SWIFT, false);
	}
}
