package org.theglobalsquare.ui;

import org.theglobalsquare.app.*;
import org.theglobalsquare.framework.TGSFragment;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MonitorFragment extends TGSFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View monitor = inflater.inflate(R.layout.main_monitor, null);
		TextView tv = (TextView)monitor.findViewById(R.id.monitor);
		tv.setText(((MainActivity)getActivity()).getMonitorTxt());
		return monitor;
	}

}
