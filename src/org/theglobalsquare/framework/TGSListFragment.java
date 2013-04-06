package org.theglobalsquare.framework;

import java.beans.*;

import com.actionbarsherlock.app.SherlockListFragment;

public class TGSListFragment extends SherlockListFragment implements PropertyChangeListener {
	public final static String TAG = "TGSList";
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO handle search and message update events coming from python side
		android.util.Log.d(TAG, "propertyChange: " + event + ", new value: " + event.getNewValue());
//		TGSMainActivity.handle(event.getNewValue());
	}

}
