package org.theglobalsquare.framework;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.*;
import java.util.concurrent.*;

// A queue of events passing between the java (UI) and python (dispersy) layers.
public class TGSEventProxy {
	private static TGSEventProxy _instance = null;
	private Map<Class<? extends TGSEvent>, Set<PropertyChangeListener>> listeners;
	private Queue<TGSEvent> qToPy;
	private TGSEventProxy() {
		listeners = new HashMap<Class<? extends TGSEvent>, Set<PropertyChangeListener>>();
		qToPy = new ConcurrentLinkedQueue<TGSEvent>();
	}
	public static TGSEventProxy getInstance() {
		if(_instance == null)
			_instance = new TGSEventProxy();
		return _instance;
	}
	public static void addListener(Class<? extends TGSEvent> c, PropertyChangeListener l) {
		TGSEventProxy proxy = getInstance();
		Set<PropertyChangeListener> ls = proxy.listeners.get(c);
		if(ls == null) {
			ls = new HashSet<PropertyChangeListener>();
			proxy.listeners.put(c, ls);
		}
		ls.add(l);
	}
	public static boolean sendEvent(TGSEvent e) {
		// python calls sendEvent(e) to send to java
		// java calls sendEvent(e, true); to send to python
		// returns false if it can't send
		return sendEvent(e, false);
	}
	public static boolean sendEvent(TGSEvent e, boolean toPy) {
		TGSEventProxy proxy = TGSEventProxy.getInstance();
		if(toPy)
			return proxy.qToPy.offer(e);
		for(Class<? extends TGSEvent> c : proxy.listeners.keySet()) {
			if(c == null // want to know all events
					|| e.getClass().isAssignableFrom(c)) { // e instanceof c
				Set<PropertyChangeListener> ls = proxy.listeners.get(c);
				for(PropertyChangeListener l : ls)
					l.propertyChange(new PropertyChangeEvent(proxy, "qFromPy", null, e));
			}
		}
		return true;
	}
	public static TGSEvent nextEvent() {
		// python calls nextEvent() to get from java
		// java uses PropertyChangeListener to get from python
		// returns null if no events
		return getInstance().qToPy.poll();
	}
}
