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

public class Facade extends Application {
	// main access point for python side to talk to Android application (as opposed to activity)
	public final static String TAG = "Facade";
	
	public final static String PREF_ALIAS = "pref_alias";
	public final static String PREF_DEFAULT_TAB = "pref_default_tab";
	public final static String PREF_ENABLE_DISPERSY = "pref_enable_dispersy";
	public final static String PREF_DISPERSY_PORT = "pref_dispersy_port";
	public final static String PREF_ENABLE_PROXY = "pref_enable_proxy";
	public final static String PREF_REQUIRE_PROXY = "pref_require_proxy";
	public final static String PREF_PROXY_HOST = "pref_proxy_host";
	public final static String PREF_PROXY_PORT = "pref_proxy_port";
	
	private static Map<Class<? extends TGSEvent>, Set<PropertyChangeListener>> sListeners = null;
	
	private TGSCommunityList mCommunities = new TGSCommunityList();
	
	public TGSCommunityList getCommunities() {
		return mCommunities;
	}

	private boolean mNewCommunities = false;
	
	public boolean hasNewCommunities() {
		return mNewCommunities;
	}
	
	public void setCommunitiesChecked() {
		mNewCommunities = false;
	}
	
	public void setCommunities(TGSCommunityList l) {
		this.mCommunities = l;
		
		// notify fragment there are new communities
		mNewCommunities = true;
	}
	
	public void addCommunity(TGSCommunity c) {
		// add this community if it doesn't exist yet
		mCommunities.addCommunity(c);
		
		// notify fragment there is a new community
		mNewCommunities = true;
	}

	@Override
	public void onCreate() {
		if(sListeners == null) {
			android.util.Log.d(Facade.TAG, "NEW LISTENERS");
			sListeners = new HashMap<Class<? extends TGSEvent>, Set<PropertyChangeListener>>();
		}

		if(qToPy == null) {
			android.util.Log.d(TGSMainActivity.TAG, "NEW QUEUE");
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
				android.util.Log.d(TGSMainActivity.TAG, "NEW QUEUE ON SEND EVENT");
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
		config.setDefaultTab(getDefaultTab());
		config.setDispersyEnabled(isDispersyEnabled());
		config.setDispersyPort(Integer.valueOf(getDispersyPort()));
		config.setProxyEnabled(isProxyEnabled());
		config.setProxyRequired(isProxyRequired());
		config.setProxyHost(getProxyHost());
		config.setProxyPort(Integer.valueOf(getProxyPort()));
	}
	
	public String getAlias() {
		return getPrefs().getString(PREF_ALIAS, getResources().getString(R.string.anonLabel));
	}
	
	public String getDefaultTab() {
		return getPrefs().getString(PREF_DEFAULT_TAB, getResources().getString(R.string.defaultTab));
	}
	
	public boolean isDispersyEnabled() {
		boolean dispersyEnabled = getPrefs().getBoolean(PREF_ENABLE_DISPERSY, true);
		android.util.Log.v(Facade.TAG, "dispersyEnabled: " + dispersyEnabled);
		return dispersyEnabled;
	}

	public String getDispersyPort() {
		return getPrefs().getString(PREF_DISPERSY_PORT, getResources().getString(R.string.dispersyPortDefault));
	}

	public boolean isProxyEnabled() {
		boolean proxyEnabled = getPrefs().getBoolean(PREF_ENABLE_PROXY, false);
		android.util.Log.v(Facade.TAG, "proxyEnabled: " + proxyEnabled);
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

}
