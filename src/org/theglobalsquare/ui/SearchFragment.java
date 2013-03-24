package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.TGSFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchFragment extends TGSFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_search, null);
	}

}
