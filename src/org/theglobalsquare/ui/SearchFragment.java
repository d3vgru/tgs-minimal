package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;
import org.theglobalsquare.app.TGSMainActivity;
import org.theglobalsquare.framework.TGSFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SearchFragment extends TGSFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// search layout
		View view = inflater.inflate(R.layout.main_search, null);
		
		// submit-on-enter
		EditText terms = (EditText)view.findViewById(R.id.txt_search_terms);
		terms.setOnKeyListener((TGSMainActivity)getActivity());
		
		return view;
	}

}
