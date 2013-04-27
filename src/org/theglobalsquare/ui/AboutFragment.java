package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ui.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends TGSDialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View about = inflater.inflate(R.layout.about, null);

		getDialog().setTitle(R.string.aboutBtnLabel);
		
		return about;
	}

}
