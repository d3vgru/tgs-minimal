package org.theglobalsquare.ui;

import org.theglobalsquare.app.*;
import org.theglobalsquare.framework.ui.TGSFragment;
import org.theglobalsquare.framework.activity.*;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MonitorFragment extends TGSFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// log windows
		View monitor = inflater.inflate(R.layout.main_monitor, null);
		
		// initialize with current logging messages
		TextView tv = (TextView)monitor.findViewById(R.id.monitor);
		tv.setText(((TGSUIActivity)getActivity()).getMonitorTxt());
		return monitor;
	}

}
