package org.theglobalsquare.framework.values;

import org.theglobalsquare.framework.*;

public class TGSConfigEvent extends TGSEvent {
	// verbs:
	// set - set a config parameter [java]
	// updated - a config parameter has been updated [python] 

	private TGSConfig subject;

	@Override
	public TGSConfig getSubject() {
		return subject;
	}

	@Override
	public void setSubject(ITGSObject subject) {
		assert subject instanceof TGSConfig;
		this.subject = (TGSConfig)subject;
	}
	
	// what is the param called?
	private String paramName;
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	// what is the param being set to?
	private String paramValue;

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
	public TGSConfigEvent() {
		type = "config";
	}
	
	public static TGSConfigEvent forGetParam(TGSConfig c) {
		TGSConfigEvent e = new TGSConfigEvent();
		e.setSubject(c);
		e.setVerb(TGSConfig.GET_PARAM);
		return e;
	}

	public static TGSConfigEvent forParamUpdated(TGSConfig c) {
		TGSConfigEvent e = new TGSConfigEvent();
		e.setSubject(c);
		e.setVerb(TGSConfig.PARAM_UPDATED);
		return e;
	}

	public static TGSConfigEvent forSetParam(TGSConfig c) {
		TGSConfigEvent e = new TGSConfigEvent();
		e.setSubject(c);
		e.setVerb(TGSConfig.SET_PARAM);
		return e;
	}
}
