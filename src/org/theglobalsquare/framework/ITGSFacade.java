package org.theglobalsquare.framework;

import java.beans.PropertyChangeListener;

import org.theglobalsquare.framework.values.TGSConfig;

import android.content.SharedPreferences;

// functionality we want to attach at the application level, and expose to python
public interface ITGSFacade {
	public final static String PREF_ALIAS = "pref_alias";
	public final static String PREF_MONITOR_ENABLED = "pref_monitor_enabled";
	public final static String PREF_DEFAULT_DRAWER = "pref_default_drawer";
	public final static String PREF_ENABLE_DISPERSY = "pref_enable_dispersy";
	public final static String PREF_DISPERSY_PORT = "pref_dispersy_port";
	public final static String PREF_ENABLE_PROXY = "pref_enable_proxy";
	public final static String PREF_REQUIRE_PROXY = "pref_require_proxy";
	public final static String PREF_PROXY_HOST = "pref_proxy_host";
	public final static String PREF_PROXY_PORT = "pref_proxy_port";
	public final static String PREF_SWIFT_ENABLED = "pref_swift_enabled";
	public final static String PREF_TUNNEL_DISPERSY_OVER_SWIFT = "pref_tunnel_dispersy_over_swift";
	
	// FIXME make all domain classes be interfaces
	// [TGSConfig, TGSEvent)

	void addListener(Class<? extends TGSEvent> c, PropertyChangeListener l);
	void removeListener(Class<? extends TGSEvent> c, PropertyChangeListener l);

	SharedPreferences getPrefs();
	TGSConfig getConfig();
	
	String getAlias();
	int getDefaultDrawer();
	void setDefaultDrawer(int drawer);
	boolean isDispersyEnabled();
	String getDispersyPort();
	boolean isProxyEnabled();
	boolean isProxyRequired();
	String getProxyHost();
	String getProxyPort();
	boolean isSwiftEnabled();
	boolean isTunnelDispersyOverSwift();
	boolean isMonitorEnabled();
	
	boolean isCommunitiesLoaded();
	void setCommunitiesLoaded(boolean communitiesLoaded);
}
