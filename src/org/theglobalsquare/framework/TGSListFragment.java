package org.theglobalsquare.framework;

import java.beans.*;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockListFragment;

public class TGSListFragment extends SherlockListFragment implements PropertyChangeListener {
	public final static String TAG = "TGSList";
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// handle search (and message?) update events coming from python side
		android.util.Log.i(TAG, "propertyChange: " + event + ", new value: " + event.getNewValue());
		Object o = event.getNewValue();
		if(o instanceof TGSEvent) {
			final TGSEvent e = (TGSEvent)o;
			android.util.Log.i(TAG, "event: " + e.getClass().getName());
			Activity activity = getActivity();
			if(activity != null) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setEmptyText(e.toString());
						setListShown(true);
					}	
				});
			}
			else android.util.Log.d(TAG, "activity null");
			// TODO handle actual results
		} else {
			android.util.Log.i(TAG, "not event: " + o.getClass().getName());
		}
//		TGSMainActivity.handle(event.getNewValue());
	}

}
