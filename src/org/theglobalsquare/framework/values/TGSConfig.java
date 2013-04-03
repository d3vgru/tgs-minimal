package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.TGSObject;

public class TGSConfig extends TGSObject {
	// verbs
	public final static String GET_PARAM = "get_param";
	public final static String SET_PARAM = "set_param";
	public final static String PARAM_UPDATED = "param_updated";
	
	private boolean proxyEnabled = false;
	
	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
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
	
	private boolean proxyRequired = true;
	
	public boolean isProxyRequired() {
		return proxyRequired;
	}

	public void setProxyRequired(boolean proxyRequired) {
		this.proxyRequired = proxyRequired;
	}
	
	

	public TGSConfig() {
		
	}
}
