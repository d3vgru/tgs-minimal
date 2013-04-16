package org.theglobalsquare.framework;

import org.theglobalsquare.app.Facade;

import com.actionbarsherlock.app.SherlockFragment;

public class TGSFragment extends SherlockFragment {
	public Facade getFacade() {
		return (Facade)getActivity().getApplication();
	}
}
