package org.theglobalsquare.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import org.kivy.android.PythonActivity;
import org.theglobalsquare.app.config.EditPreferences;
import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.*;

public class Facade extends Application implements ITGSFacade {
	// main access point for python side to talk to Android application (as opposed to activity)
	public final static String TAG = "Facade";
	
	// if not static, then we can't call static event methods :)
	private static Queue<TGSEvent> sQToPy = null;
	private static Map<Class<? extends TGSEvent>, Set<PropertyChangeListener>> sListeners = null;	
	
	@Override
	public void onCreate() {
		if(sListeners == null) {
			android.util.Log.d(TAG, "NEW LISTENERS");
			sListeners = new HashMap<Class<? extends TGSEvent>, Set<PropertyChangeListener>>();
		}

		if(sQToPy == null) {
			android.util.Log.d(TAG, "NEW QUEUE");
			sQToPy = new ConcurrentLinkedQueue<TGSEvent>();
		}
		
		mCommunitiesLoaded = false;

		super.onCreate();
	}
	
	public static ITGSActivity getTGSActivity() {
		PythonActivity a = PythonActivity.mActivity;
		if(a instanceof ITGSActivity)
			return ((ITGSActivity)a);
		return null;
	}
	
	public static int queueSize() {
		return sQToPy.size();
	}

	public static TGSEvent nextEvent() {
		return sQToPy.poll();
	}

	public static boolean sendEvent(TGSEvent e) {
		return sendEvent(e, false);
	}
	public static boolean sendEvent(TGSEvent e, boolean toPy) {
		if(toPy) {
			if(sQToPy == null) {
				android.util.Log.d(TAG, "NEW QUEUE ON SEND EVENT");
				sQToPy = new ConcurrentLinkedQueue<TGSEvent>();
			}
			boolean success = sQToPy.add(e);
			return success;
		}
		if(e instanceof TGSListEvent) {
			ITGSList objList = ((TGSListEvent)e).getList();
			if(objList instanceof TGSCommunityList) {
				ITGSActivity a = getTGSActivity();
				if(a != null) {
					ITGSFacade f = a.getTGSFacade();
					f.setCommunitiesLoaded(true);
				}
			}
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

	private static TGSConfig sConfig = null;
	
	@SuppressLint("InlinedApi")
	public SharedPreferences getPrefs() {
		// multi process or changes won't be reflected immediately
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			return getSharedPreferences(EditPreferences.SHARED_PREFS_KEY, Context.MODE_MULTI_PROCESS);
		return getSharedPreferences(EditPreferences.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
	}
	
	public TGSConfig getConfig() {
		if(sConfig == null) {
			sConfig = new TGSConfig();
		}
		refreshConfig();
		return sConfig;
	}
	
	private void refreshConfig() {
		String alias = getAlias();
		sConfig.setName(alias);
		TGSUser.getMe().setName(alias);
		sConfig.setMonitorEnabled(isMonitorEnabled());
		sConfig.setDefaultDrawer(getDefaultDrawer());
		sConfig.setDispersyEnabled(isDispersyEnabled());
		sConfig.setDispersyPort(Integer.valueOf(getDispersyPort()));
		sConfig.setProxyEnabled(isProxyEnabled());
		sConfig.setProxyRequired(isProxyRequired());
		sConfig.setProxyHost(getProxyHost());
		sConfig.setProxyPort(Integer.valueOf(getProxyPort()));
		sConfig.setSwiftEnabled(isSwiftEnabled());
		sConfig.setTunnelDispersyOverSwift(isTunnelDispersyOverSwift());
	}
	
	@Override
	public String getAlias() {
		return getPrefs().getString(PREF_ALIAS, getResources().getString(R.string.anonLabel));
	}
	
	@Override
	public boolean isMonitorEnabled() {
		return getPrefs().getBoolean(PREF_MONITOR_ENABLED, false);
	}
	
	@Override
	public int getDefaultDrawer() {
		return getPrefs().getInt(PREF_DEFAULT_DRAWER, ITGSActivity.DRAWER_OVERVIEW);
	}
	
	@Override
	public void setDefaultDrawer(int drawer) {
		Editor prefs = getPrefs().edit();
		prefs.putInt(PREF_DEFAULT_DRAWER, drawer);
		prefs.commit();
	}
	
	@Override
	public boolean isDispersyEnabled() {
		boolean dispersyEnabled = getPrefs().getBoolean(PREF_ENABLE_DISPERSY, true);
		android.util.Log.v(TAG, "dispersyEnabled: " + dispersyEnabled);
		return dispersyEnabled;
	}

	@Override
	public String getDispersyPort() {
		return getPrefs().getString(PREF_DISPERSY_PORT, getResources().getString(R.string.dispersyPortDefault));
	}

	@Override
	public boolean isProxyEnabled() {
		boolean proxyEnabled = getPrefs().getBoolean(PREF_ENABLE_PROXY, false);
		android.util.Log.v(TAG, "proxyEnabled: " + proxyEnabled);
		return proxyEnabled;
	}

	@Override
	public boolean isProxyRequired() {
		return getPrefs().getBoolean(PREF_REQUIRE_PROXY, false);
	}
	
	@Override
	public String getProxyHost() {
		return getPrefs().getString(PREF_PROXY_HOST, getResources().getString(R.string.localhostLabel));
	}
	
	@Override
	public String getProxyPort() {
		return getPrefs().getString(PREF_PROXY_PORT, getResources().getString(R.string.proxyPortDefault));
	}
	
	@Override
	public boolean isSwiftEnabled() {
		return getPrefs().getBoolean(PREF_SWIFT_ENABLED, false);
	}

	@Override
	public boolean isTunnelDispersyOverSwift() {
		return getPrefs().getBoolean(PREF_TUNNEL_DISPERSY_OVER_SWIFT, false);
	}

	private boolean mCommunitiesLoaded;
	
	@Override
	public boolean isCommunitiesLoaded() {
		return mCommunitiesLoaded;
	}

	@Override
	public void setCommunitiesLoaded(boolean communitiesLoaded) {
		mCommunitiesLoaded = communitiesLoaded;
	}
}
