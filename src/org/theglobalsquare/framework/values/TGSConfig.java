package org.theglobalsquare.framework.values;

import org.json.JSONException;
import org.json.JSONObject;
import org.theglobalsquare.framework.TGSObject;

public class TGSConfig extends TGSObject {
	// verbs
	public final static String GET_PARAM = "get_param";
	public final static String SET_PARAM = "set_param";
	public final static String PARAM_UPDATED = "param_updated";
	
	private boolean monitorEnabled;
	
	public boolean isMonitorEnabled() {
		return monitorEnabled;
	}
	
	public void setMonitorEnabled(boolean monitorEnabled) {
		this.monitorEnabled = monitorEnabled;
	}
	
	private int defaultDrawer;
	
	public int getDefaultDrawer() {
		return defaultDrawer;
	}

	public void setDefaultDrawer(int defaultDrawer) {
		this.defaultDrawer = defaultDrawer;
	}

	private boolean dispersyEnabled = false;
	
	public boolean isDispersyEnabled() {
		return dispersyEnabled;
	}

	public void setDispersyEnabled(boolean dispersyEnabled) {
		this.dispersyEnabled = dispersyEnabled;
	}

	private Integer dispersyPort;

	public Integer getDispersyPort() {
		return dispersyPort;
	}

	public void setDispersyPort(Integer dispersyPort) {
		this.dispersyPort = dispersyPort;
	}
	
	private boolean proxyEnabled = false;
	
	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	private boolean proxyRequired = true;
	
	public boolean isProxyRequired() {
		return proxyRequired;
	}

	public void setProxyRequired(boolean proxyRequired) {
		this.proxyRequired = proxyRequired;
	}
	
	private String proxyHost;
	
	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	private Integer proxyPort;

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}
	
	boolean swiftEnabled;

	public boolean isSwiftEnabled() {
		return swiftEnabled;
	}

	public void setSwiftEnabled(boolean swiftEnabled) {
		this.swiftEnabled = swiftEnabled;
	}

	boolean tunnelDispersyOverSwift;
	
	public boolean isTunnelDispersyOverSwift() {
		return tunnelDispersyOverSwift;
	}

	public void setTunnelDispersyOverSwift(boolean tunnelDispersyOverSwift) {
		this.tunnelDispersyOverSwift = tunnelDispersyOverSwift;
	}

	
	public TGSConfig() {
		
	}

	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject o = super.toJsonObject();
		o.put("defaultDrawer", getDefaultDrawer());
		o.put("dispersyEnabled", isDispersyEnabled());
		o.put("dispersyPort", getDispersyPort());
		o.put("proxyEnabled", isProxyEnabled());
		o.put("proxyRequired", isProxyRequired());
		o.put("proxyHost", getProxyHost());
		o.put("proxyPort", getProxyPort());
		o.put("swiftEnabled", isSwiftEnabled());
		o.put("tunnelDispersyOverSwift", isTunnelDispersyOverSwift());
		return o;
	}
	
}
