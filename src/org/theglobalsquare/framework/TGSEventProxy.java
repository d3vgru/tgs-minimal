package org.theglobalsquare.framework;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.*;
import java.util.concurrent.*;

// A queue of events passing between the java (UI) and python (dispersy) layers.
public class TGSEventProxy {
	private Map<Class<? extends TGSEvent>, Set<PropertyChangeListener>> listeners;
	private Queue<TGSEvent> qToPy;
	public TGSEventProxy() {
		listeners = new HashMap<Class<? extends TGSEvent>, Set<PropertyChangeListener>>();
		qToPy = new ConcurrentLinkedQueue<TGSEvent>();
	}
	public void addListener(Class<? extends TGSEvent> c, PropertyChangeListener l) {
		Set<PropertyChangeListener> ls = this.listeners.get(c);
		if(ls == null) {
			ls = new HashSet<PropertyChangeListener>();
			this.listeners.put(c, ls);
		}
		ls.add(l);
	}
	public boolean sendEvent(TGSEvent e) {
		// python calls sendEvent(e) to send to java
		// java calls sendEvent(e, true); to send to python
		// returns false if it can't send
		return sendEvent(e, false);
	}
	public boolean sendEvent(TGSEvent e, boolean toPy) {
		if(toPy)
			return this.qToPy.offer(e);
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
	public TGSEvent nextEvent() {
		// python calls nextEvent() to get from java
		// java uses PropertyChangeListener to get from python
		// returns null if no events
		return this.qToPy.poll();
	}
	
	public int size() {
		return this.qToPy.size();
	}
}
