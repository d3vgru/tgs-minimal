package org.theglobalsquare.framework.ui;

import org.theglobalsquare.framework.ITGSActivity;
import org.theglobalsquare.framework.ITGSFacade;

import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragment;

public class TGSFragment extends SherlockFragment {
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.setBackgroundColor(getResources().getColor(android.R.color.white));
		super.onViewCreated(view, savedInstanceState);
	}

	public ITGSActivity getTGSActivity() {
		return (ITGSActivity)getActivity();
	}
	
	public ITGSFacade getTGSFacade() {
		return getTGSActivity().getTGSFacade();
	}
	
}
