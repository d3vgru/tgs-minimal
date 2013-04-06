package org.theglobalsquare.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.widget.*;

import org.kivy.android.PythonActivity;
import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.*;

// this class defines interactions between java and python layers
public class TGSMainActivity extends TGSUIActivity implements PropertyChangeListener {
	public final static String TAG = "TGSMain";

	private static Handler msgHandler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get events from python (or else AndroidFacade in python won't be able to send us TGSSystemEvent)
		getFacade().addListener(TGSSystemEvent.class, this);		

		monitor(TGSMainActivity.TAG + ": INIT");
	}

	@Override
	protected void onResume() {
		PythonActivity.mActivity = this;
		//freshenConfig();
		super.onResume();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		android.util.Log.d(TGSMainActivity.TAG, "propertyChange: " + event + ", new value: " + event.getNewValue());
		TGSMainActivity.handle(event.getNewValue());
	}

	public static Handler getHandler() {
		if(msgHandler == null) {
			// initialize UI event handler
			if(PythonActivity.mActivity == null) {
				android.util.Log.w(TGSMainActivity.TAG, "mActivity is null");
			} else {
				msgHandler = new MessageHandler((TGSMainActivity)PythonActivity.mActivity);
			}
		}
		return msgHandler;
	}

	// http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
	static class MessageHandler extends Handler {
		private final WeakReference<TGSMainActivity> mActivity;

		MessageHandler(TGSMainActivity service) {
			super(Looper.getMainLooper());
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

	// generic method to pass an object that needs to be handled by the UI
	public static void handle(Object obj) {
		Handler msgHandler = getHandler();
		if (msgHandler != null) {
			android.util.Log.d(TGSMainActivity.TAG, "msgHandler not null");
			Message msg = new Message();
			android.util.Log.v(TGSMainActivity.TAG,
					"android.os.Message obtained: " + msg);
			msg.obj = obj;
			android.util.Log.v(TGSMainActivity.TAG, "obj field of Message set");
			msgHandler.sendMessage(msg);
			android.util.Log.v(TGSMainActivity.TAG, "logged");
		} else android.util.Log.d(TGSMainActivity.TAG, "msgHandler null");
	}

	// to be used by python
	public static void log(String message) {
		// log() lets you see the event in logcat
		android.util.Log.i(TGSMainActivity.TAG, "got message from python: " + message);
		handle(message);
	}

	public void monitor(String message) {
		// monitor() shows the event in the monitor tab
		super.monitor(message);
	}

	// ITGSActivity impl
	public void submitSearch(EditText et) {
		String term = "";
		Editable e = et.getText();
		if(e != null)
			term = e.toString();
		et.setText(null);
		dismissKeyboardFor(et);

		// make a search event and put it in the queue
		TGSCommunity c = new TGSCommunity();
		c.setName(term);
		TGSSearchEvent s = new TGSCommunitySearchEvent();
		s.setSubject(c);
		Facade.sendEvent(s, true);
		
		android.util.Log.i(TGSUIActivity.TAG, "search: " + term);
	}
	
}
