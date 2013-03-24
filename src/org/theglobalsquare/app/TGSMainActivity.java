package org.theglobalsquare.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.*;

import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.*;

// this class defines interactions between java and python layers
public class TGSMainActivity extends UIShellActivity implements
		PropertyChangeListener {
	public final static String TAG = "TGSMain";

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		android.util.Log.d(TGSMainActivity.TAG, "propertyChange:" + event);
		if (msgHandler == null)
			return;
		Message message = new Message();
		android.util.Log.v(TGSMainActivity.TAG, "android.os.Message obtained:"
				+ message);
		message.obj = event.getNewValue();
		msgHandler.sendMessage(message);
	}

	private static Handler msgHandler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		monitor(TGSMainActivity.TAG + ": INIT");

		// initialize UI event handler
		msgHandler = new MessageHandler(this);

		// get events from python (or else AndroidFacade won't be able to send us events)
		events.addListener(TGSSystemEvent.class, this);
	}

	// http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
	static class MessageHandler extends Handler {
		private final WeakReference<TGSMainActivity> mActivity;

		MessageHandler(TGSMainActivity service) {
			mActivity = new WeakReference<TGSMainActivity>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			// the TGSEvent object
			Object value = msg.obj;
			String out = "";
			TGSMainActivity activity = mActivity.get();
			if (activity == null) {
				android.util.Log.w(TGSMainActivity.TAG + ".MsgHandler",
						"weak ref to activity was null");
				return;
			}

			android.util.Log.v(TGSMainActivity.TAG + ".MsgHandler", "value: "
					+ value);
			android.util.Log.v(TGSMainActivity.TAG + ".MsgHandler", "class: "
					+ value.getClass().getName());

			// turn LED green on dispersy start
			if (value instanceof TGSEvent) {
				TGSEvent event = (TGSEvent) value;
				out = "EVENT: " + value.getClass().getSimpleName() + ": "
						+ event.toString();
				if (value instanceof TGSSystemEvent) {
					 if(TGSMessage.START.equals(event.getVerb())) {
						 ((ImageView)activity.findViewById(R.id.statusLight))
					 		.setImageResource(R.drawable.led_green); }
					if (TGSMessage.LOG.equals(event.getVerb())) {
						out = "LOG: "
								+ ((TGSMessage) event.getSubject()).getBody();
						android.util.Log.i(TGSMainActivity.TAG, out);
					}
				}
			} else if (value instanceof TGSObject) {
				out = "OBJECT: " + ((TGSObject) value).getName() + ": " + value;
			} else {
				if (value != null) {
					activity.monitor(value.toString());
				}
				return;
			}
			if (activity != null)
				activity.monitor(out);
		}
	}

	// to be used by python
	public static void log(String message) {
		// log() lets you see the event in logcat
		android.util.Log.i(TGSMainActivity.TAG, "got message from python: "
				+ message);
		if (msgHandler != null) {
			android.util.Log.i(TGSMainActivity.TAG, "msgHandler not null");
			Message msg = new Message();
			android.util.Log.i(TGSMainActivity.TAG,
					"android.os.Message obtained: " + msg);
			msg.obj = message;
			android.util.Log.i(TGSMainActivity.TAG, "obj field of Message set");
			msgHandler.sendMessage(msg);
			android.util.Log.i(TGSMainActivity.TAG, "logged");
		}
	}

	public void monitor(String message) {
		// monitor() shows the event in the monitor tab
		super.monitor(message);
	}

	public TGSEventProxy getEvents() {
		return events;
	}

	public static boolean sendEvent(TGSEvent e) {
		if (UIShellActivity.events == null)
			return false;
		UIShellActivity.events.sendEvent(e);
		return true;
	}
}
